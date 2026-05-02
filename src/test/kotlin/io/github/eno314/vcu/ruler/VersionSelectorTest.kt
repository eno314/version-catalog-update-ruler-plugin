package io.github.eno314.vcu.ruler

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.logging.Logger
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class VersionSelectorTest {
    @InjectMockKs
    private lateinit var versionSelector: VersionSelector

    @MockK
    private lateinit var extension: VersionCatalogUpdateRulerExtension

    @MockK(relaxed = true)
    private lateinit var versionParser: VersionParser

    @MockK(relaxed = true)
    private lateinit var candidateValidator: VersionCandidateValidator

    @MockK(relaxed = true)
    private lateinit var logger: Logger

    private val candidate =
        ModuleVersionCandidate(
            currentVersion = "1.0.0",
            candidate =
                object : ModuleComponentIdentifier {
                    override fun getGroup(): String = "com.example"

                    override fun getModule(): String = "test-module"

                    override fun getVersion(): String = "1.1.1-test"

                    override fun getModuleIdentifier(): ModuleIdentifier = mockk()

                    override fun getDisplayName(): String = "com.example:test-module:test-version"
                },
        )

    private val currentVersion = ArtifactVersion(1, 0, listOf(0))
    private val candidateVersion = ArtifactVersion(1, 1, listOf(0))

    @BeforeEach
    fun setup() {
        every { extension.onlyStable.get() } returns false
        every { extension.unStableVersionRegex.get() } returns Regex(".*")
        every { extension.onlyArtifactVersion.get() } returns false
        every { extension.pinMajorVersion.get() } returns false
        every { extension.pinMinorVersion.get() } returns false

        val libraryRules = mockk<NamedDomainObjectContainer<LibraryUpdateRule>>()
        every { extension.libraryRules } returns libraryRules
        every { libraryRules.findByName(any<String>()) } returns null
    }

    @Test
    fun `select returns true when isSelectableVersion(onlyStable is false) and isUpdatableVersion(onlyArtifactVersion is false)`() {
        every { extension.onlyStable.get() } returns false
        every { extension.pinMajorVersion.get() } returns true
        every { extension.pinMinorVersion.get() } returns false
        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every {
            candidateValidator.isValidCandidate(
                currentVersion,
                candidateVersion,
                pinMajorVersion = true,
                pinMinorVersion = false,
            )
        } returns true

        val actual = versionSelector.select(candidate)

        assertTrue(actual)
    }

    @Test
    fun `select returns true when isSelectableVersion(candidate is stable version) and isUpdatableVersion(isValidCandidate is true)`() {
        val stableCandidate =
            ModuleVersionCandidate(
                currentVersion = "1.0.0",
                candidate =
                    object : ModuleComponentIdentifier {
                        override fun getGroup(): String = "com.example"

                        override fun getModule(): String = "test-module"

                        override fun getVersion(): String = "1.1.1"

                        override fun getModuleIdentifier(): ModuleIdentifier = mockk()

                        override fun getDisplayName(): String = "com.example:test-module:1.1.1"
                    },
            )

        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc|test|-M\\d+).*", RegexOption.IGNORE_CASE)
        every { versionParser.parse(stableCandidate.currentVersion) } returns currentVersion
        every { versionParser.parse(stableCandidate.candidate.version) } returns candidateVersion
        every { extension.onlyArtifactVersion.get() } returns false

        every {
            candidateValidator.isValidCandidate(
                any(),
                any(),
                any(),
                any(),
            )
        } returns true

        val actual = versionSelector.select(stableCandidate)

        assertTrue(actual)
    }

    @Test
    fun `select returns false when isSelectableVersion is false(onlyStable is true and candidate is milestone version)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc|test|-M\\d+).*", RegexOption.IGNORE_CASE)

        val milestoneCandidate =
            ModuleVersionCandidate(
                currentVersion = "1.0.0",
                candidate =
                    object : ModuleComponentIdentifier {
                        override fun getGroup(): String = "com.example"

                        override fun getModule(): String = "test-module"

                        override fun getVersion(): String = "1.1.0-M4"

                        override fun getModuleIdentifier(): ModuleIdentifier = mockk()

                        override fun getDisplayName(): String = "com.example:test-module:1.1.0-M4"
                    },
            )

        val actual = versionSelector.select(milestoneCandidate)

        assertFalse(actual)
    }

    @Test
    fun `select returns false when isSelectableVersion is false(onlyStable is true and candidate isn't stable version)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc|test|-M\\d+).*", RegexOption.IGNORE_CASE)

        val actual = versionSelector.select(candidate)

        assertFalse(actual)
    }

    @Test
    fun `select returns false when isUpdatableVersion is false(version is not artifact version and onlyArtifactVersion is true)`() {
        every { extension.onlyStable.get() } returns false
        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns null
        every { extension.onlyArtifactVersion.get() } returns true

        val actual = versionSelector.select(candidate)

        assertFalse(actual)
    }

    @Test
    fun `select returns false when isUpdatableVersion is false(isValidCandidate is false)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc|test|-M\\d+).*", RegexOption.IGNORE_CASE)
        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every {
            candidateValidator.isValidCandidate(
                currentVersion,
                candidateVersion,
                pinMajorVersion = true,
                pinMinorVersion = false,
            )
        } returns false

        val actual = versionSelector.select(candidate)

        assertFalse(actual)
    }

    @Test
    fun `select uses library specific rule when available`() {
        val libraryRule = mockk<LibraryUpdateRule>()
        every { extension.libraryRules.findByName("com.example:test-module") } returns libraryRule

        every { libraryRule.onlyStable.get() } returns true
        every { libraryRule.unStableVersionRegex.get() } returns Regex(".*(beta).*")
        every { libraryRule.pinMajorVersion.get() } returns false
        every { libraryRule.pinMinorVersion.get() } returns false
        every { libraryRule.onlyArtifactVersion.get() } returns false

        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every {
            candidateValidator.isValidCandidate(
                currentVersion,
                candidateVersion,
                pinMajorVersion = false,
                pinMinorVersion = false,
            )
        } returns true

        // candidate version is "1.1.1-test"
        // libraryRule.unStableVersionRegex is ".*(beta).*"
        // "1.1.1-test" does not match ".*(beta).*"
        // so it should be considered stable for this library rule

        val actual = versionSelector.select(candidate)

        assertTrue(actual)
    }
}
