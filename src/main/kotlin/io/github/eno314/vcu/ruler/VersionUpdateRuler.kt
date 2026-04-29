package io.github.eno314.vcu.ruler

internal class VersionUpdateRuler {
    fun shouldUpdate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ): Boolean =
        (!pinMajorVersion && isMajorUpdate(currentVersion, candidateVersion)) ||
            (!pinMinorVersion && isMinorUpdate(currentVersion, candidateVersion)) ||
            isPatchUpdate(currentVersion, candidateVersion)

    private fun isMajorUpdate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
    ): Boolean = currentVersion.major < candidateVersion.major

    private fun isMinorUpdate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
    ): Boolean {
        if (currentVersion.minor == null || candidateVersion.minor == null) {
            return false
        }
        return currentVersion.major == candidateVersion.major &&
            currentVersion.minor < candidateVersion.minor
    }

    private fun isPatchUpdate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
    ): Boolean {
        if (currentVersion.patch == null || candidateVersion.patch == null) {
            return false
        }
        return currentVersion.major == candidateVersion.major &&
            currentVersion.minor == candidateVersion.minor &&
            comparePatchLists(currentVersion.patch, candidateVersion.patch) < 0
    }

    /**
     * Compares two patch version lists element by element.
     * Missing trailing elements are treated as 0.
     * Returns negative if current < candidate, positive if current > candidate, 0 if equal.
     */
    private fun comparePatchLists(
        current: List<Int>,
        candidate: List<Int>,
    ): Int {
        val maxLen = maxOf(current.size, candidate.size)
        for (i in 0 until maxLen) {
            val diff = current.getOrElse(i) { 0 } - candidate.getOrElse(i) { 0 }
            if (diff != 0) return diff
        }
        return 0
    }
}
