package jp.eno314.vcu.ruler

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.gradle.internal.impldep.junit.framework.TestCase.assertFalse
import org.gradle.internal.impldep.junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExtendWith(MockKExtension::class)
class VersionUpdateRulerTest {
    @InjectMockKs
    private lateinit var versionUpdateRuler: VersionUpdateRuler

    private val currentVersion = ArtifactVersion(1, 2, 3.0)

    @ParameterizedTest
    @CsvSource(
        value = [
            "2, 0, 3.1, false",
            "3, 3, 4.0, false",
            "2, 3,    , true",
            "3,  ,    , true",
        ],
    )
    fun `shouldUpdate returns true when allowedMajor and majorUpdate`(
        candidateMajor: Int,
        candidateMinor: Int?,
        candidatePatch: Double?,
        pinMinorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(candidateMajor, candidateMinor, candidatePatch)
        val pinMajorVersion = false

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "2, 0, 3.1, false",
            "3, 3, 4.0, false",
            "2, 3,    , true",
            "3,  ,    , true",
        ],
    )
    fun `shouldUpdate returns false when pinMajor and majorUpdate`(
        candidateMajor: Int,
        candidateMinor: Int?,
        candidatePatch: Double?,
        pinMinorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(candidateMajor, candidateMinor, candidatePatch)
        val pinMajorVersion = true

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertFalse(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3, 3.1, false",
            "4, 4.0, false",
            "3,    , true",
        ],
    )
    fun `shouldUpdate returns true when allowedMinor and minorUpdate`(
        candidateMinor: Int,
        candidatePatch: Double?,
        pinMajorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(1, candidateMinor, candidatePatch)
        val pinMinorVersion = false

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3, 3.1, false",
            "4, 4.0, false",
            "4,    , true",
        ],
    )
    fun `shouldUpdate returns false when pinMinor and minorUpdate`(
        candidateMinor: Int,
        candidatePatch: Double?,
        pinMajorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(1, candidateMinor, candidatePatch)
        val pinMinorVersion = true

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertFalse(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3.1, true, true",
            "4.0, true, false",
            "3.01, false, true",
            "10.0, false, false",
        ],
    )
    fun `shouldUpdate returns true when patchUpdate`(
        candidatePatch: Double,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(1, 2, candidatePatch)

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "true, true",
            "true, false",
            "false, true",
            "false, false",
        ],
    )
    fun `shouldUpdate returns false when same version`(
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(1, 2, 3.0)

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertFalse(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "0,  , , true, true",
            "1,  , , true, false",
            "1, 1, , false, true",
            "1, 2, , false, false",
            "1, 2, 2.9, false, false",
        ],
    )
    fun `shouldUpdate returns false when downgrade`(
        candidateMajor: Int,
        candidateMinor: Int?,
        candidatePatch: Double?,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ) {
        // Given
        val candidateVersion = ArtifactVersion(candidateMajor, candidateMinor, candidatePatch)

        // When
        val result = versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

        // Then
        assertFalse(result)
    }

    @Test
    fun `shouldUpdate returns false when only major version and pinMajorVersion`() {
        val current = ArtifactVersion(1, null, null)
        val candidate = ArtifactVersion(2, null, null)

        // When
        val result = versionUpdateRuler.shouldUpdate(current, candidate, true, false)

        // Then
        assertFalse(result)
    }
}
