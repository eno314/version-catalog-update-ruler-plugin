package io.github.eno314.vcu.ruler

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class VersionUpdateRulerTest {
    @InjectMockKs
    private lateinit var versionUpdateRuler: VersionUpdateRuler

    private val currentVersion = ArtifactVersion(1, 2, listOf(3))

    // --- shouldUpdate returns true when allowedMajor and majorUpdate ---

    @Test
    fun `shouldUpdate returns true when allowedMajor and majorUpdate - with patch and pinMinor false`() {
        val candidateVersion = ArtifactVersion(2, 0, listOf(3, 1))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    @Test
    fun `shouldUpdate returns true when allowedMajor and majorUpdate - with higher patch and pinMinor false`() {
        val candidateVersion = ArtifactVersion(3, 3, listOf(4))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    @Test
    fun `shouldUpdate returns true when allowedMajor and majorUpdate - no patch and pinMinor true`() {
        val candidateVersion = ArtifactVersion(2, 3, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns true when allowedMajor and majorUpdate - no minor no patch and pinMinor true`() {
        val candidateVersion = ArtifactVersion(3, null, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    // --- shouldUpdate returns false when pinMajor and majorUpdate ---

    @Test
    fun `shouldUpdate returns false when pinMajor and majorUpdate - with patch and pinMinor false`() {
        val candidateVersion = ArtifactVersion(2, 0, listOf(3, 1))
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    @Test
    fun `shouldUpdate returns false when pinMajor and majorUpdate - with higher patch and pinMinor false`() {
        val candidateVersion = ArtifactVersion(3, 3, listOf(4))
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    @Test
    fun `shouldUpdate returns false when pinMajor and majorUpdate - no patch and pinMinor true`() {
        val candidateVersion = ArtifactVersion(2, 3, null)
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    @Test
    fun `shouldUpdate returns false when pinMajor and majorUpdate - no minor no patch and pinMinor true`() {
        val candidateVersion = ArtifactVersion(3, null, null)
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    // --- shouldUpdate returns true when allowedMinor and minorUpdate ---

    @Test
    fun `shouldUpdate returns true when allowedMinor and minorUpdate - with patch and pinMajor false`() {
        val candidateVersion = ArtifactVersion(1, 3, listOf(3, 1))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    @Test
    fun `shouldUpdate returns true when allowedMinor and minorUpdate - with higher patch and pinMajor false`() {
        val candidateVersion = ArtifactVersion(1, 4, listOf(4))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    @Test
    fun `shouldUpdate returns true when allowedMinor and minorUpdate - no patch and pinMajor true`() {
        val candidateVersion = ArtifactVersion(1, 3, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    // --- shouldUpdate returns false when pinMinor and minorUpdate ---

    @Test
    fun `shouldUpdate returns false when pinMinor and minorUpdate - with patch and pinMajor false`() {
        val candidateVersion = ArtifactVersion(1, 3, listOf(3, 1))
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns false when pinMinor and minorUpdate - with higher patch and pinMajor false`() {
        val candidateVersion = ArtifactVersion(1, 4, listOf(4))
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns false when pinMinor and minorUpdate - no patch and pinMajor true`() {
        val candidateVersion = ArtifactVersion(1, 4, null)
        assertFalse(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    // --- shouldUpdate returns true when patchUpdate ---

    @Test
    fun `shouldUpdate returns true when patchUpdate - patch 3_1 with both pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3, 1))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    @Test
    fun `shouldUpdate returns true when patchUpdate - patch 4 with pinMajor only`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(4))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    @Test
    fun `shouldUpdate returns true when patchUpdate - patch 3_0_1 with pinMinor only`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3, 0, 1))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns true when patchUpdate - patch 10 with nothing pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(10))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    // --- shouldUpdate returns true when same version (allowed to prevent "could not be resolved") ---

    @Test
    fun `shouldUpdate returns true when same version - both pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    @Test
    fun `shouldUpdate returns true when same version - pinMajor only`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    @Test
    fun `shouldUpdate returns true when same version - pinMinor only`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns true when same version - nothing pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(3))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    // --- shouldUpdate returns true when downgrade (allowed to prevent "could not be resolved") ---

    @Test
    fun `shouldUpdate returns true when downgrade - lower major with both pinned`() {
        val candidateVersion = ArtifactVersion(0, null, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, true))
    }

    @Test
    fun `shouldUpdate returns true when downgrade - same major no minor with pinMajor only`() {
        val candidateVersion = ArtifactVersion(1, null, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, true, false))
    }

    @Test
    fun `shouldUpdate returns true when downgrade - lower minor with pinMinor only`() {
        val candidateVersion = ArtifactVersion(1, 1, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, true))
    }

    @Test
    fun `shouldUpdate returns true when downgrade - same minor no patch with nothing pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, null)
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    @Test
    fun `shouldUpdate returns true when downgrade - lower patch with nothing pinned`() {
        val candidateVersion = ArtifactVersion(1, 2, listOf(2, 9))
        assertTrue(versionUpdateRuler.shouldUpdate(currentVersion, candidateVersion, false, false))
    }

    // --- only major version ---

    @Test
    fun `shouldUpdate returns false when only major version and pinMajorVersion`() {
        val current = ArtifactVersion(1, null, null)
        val candidate = ArtifactVersion(2, null, null)

        // When
        val result = versionUpdateRuler.shouldUpdate(current, candidate, true, false)

        // Then
        assertFalse(result)
    }

    // --- Bug fix: patch list comparison ---

    @Test
    fun `shouldUpdate correctly compares 1_2_3_10 as newer than 1_2_3_4`() {
        val current = ArtifactVersion(1, 2, listOf(3, 4))
        val candidate = ArtifactVersion(1, 2, listOf(3, 10))
        assertTrue(versionUpdateRuler.shouldUpdate(current, candidate, true, true))
    }

    @Test
    fun `shouldUpdate correctly compares 1_2_3_4 as older than 1_2_3_10`() {
        val current = ArtifactVersion(1, 2, listOf(3, 10))
        val candidate = ArtifactVersion(1, 2, listOf(3, 4))
        // candidate is older than current → allowed (isOlderOrEqual returns true)
        assertTrue(versionUpdateRuler.shouldUpdate(current, candidate, true, true))
    }

    @Test
    fun `shouldUpdate correctly compares patch with different lengths - 3 vs 3_1`() {
        val current = ArtifactVersion(1, 2, listOf(3))
        val candidate = ArtifactVersion(1, 2, listOf(3, 1))
        assertTrue(versionUpdateRuler.shouldUpdate(current, candidate, true, true))
    }

    @Test
    fun `shouldUpdate correctly compares patch with different lengths - 3_1 vs 3`() {
        val current = ArtifactVersion(1, 2, listOf(3, 1))
        val candidate = ArtifactVersion(1, 2, listOf(3))
        // candidate is older than current → allowed (isOlderOrEqual returns true)
        assertTrue(versionUpdateRuler.shouldUpdate(current, candidate, true, true))
    }
}
