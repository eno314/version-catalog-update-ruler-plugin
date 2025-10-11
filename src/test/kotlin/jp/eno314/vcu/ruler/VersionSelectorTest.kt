package jp.eno314.vcu.ruler

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate
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
    private lateinit var versionUpdateRuler: VersionUpdateRuler

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

    private val currentVersion = ArtifactVersion(1, 0, 0.0)
    private val candidateVersion = ArtifactVersion(1, 1, 0.0)

    @BeforeEach
    fun setup() {
        every { extension.onlyStable } returns mockk(relaxed = true)
        every { extension.unStableVersionRegex } returns mockk(relaxed = true)
        every { extension.onlyArtifactVersion } returns mockk(relaxed = true)
        every { extension.pinMajorVersion } returns mockk()
        every { extension.pinMajorVersion.get() } returns true
        every { extension.pinMinorVersion } returns mockk()
        every { extension.pinMinorVersion.get() } returns false
    }

    @Test
    fun `select returns true when isSelectableVersion(onlyStable is false) and isUpdatableVersion(onlyArtifactVersion is false)`() {
        every { extension.onlyStable.get() } returns false
        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every {
            versionUpdateRuler.shouldUpdate(
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
    fun `select returns true when isSelectableVersion(candidate is stable version) and isUpdatableVersion(shouldUpdate is true)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc).*")
        every { versionParser.parse(candidate.currentVersion) } returns null
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every { extension.onlyArtifactVersion.get() } returns false

        val actual = versionSelector.select(candidate)

        assertTrue(actual)
    }

    @Test
    fun `select returns false when isSelectableVersion is false(onlyStable is true and candidate isn't stable version)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc|test).*")

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
    fun `select returns false when isUpdatableVersion is false(shouldUpdate is false)`() {
        every { extension.onlyStable.get() } returns true
        every { extension.unStableVersionRegex.get() } returns Regex(".*(alpha|beta|rc).*")
        every { versionParser.parse(candidate.currentVersion) } returns currentVersion
        every { versionParser.parse(candidate.candidate.version) } returns candidateVersion
        every {
            versionUpdateRuler.shouldUpdate(
                currentVersion,
                candidateVersion,
                pinMajorVersion = true,
                pinMinorVersion = false,
            )
        } returns false

        val actual = versionSelector.select(candidate)

        assertFalse(actual)
    }
}
