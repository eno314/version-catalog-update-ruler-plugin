package jp.eno314.vcu.ruler

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
            currentVersion.patch < candidateVersion.patch
    }
}
