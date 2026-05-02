package io.github.eno314.vcu.ruler

internal class VersionCandidateValidator {
    fun isValidCandidate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ): Boolean =
        // 現在のバージョン以下なら常に許可（Gradleが "could not be resolved" で落ちるのを防ぐため）
        isOlderOrEqual(candidateVersion, currentVersion) ||
            // ピン留めルールに違反していない新しいバージョンを許可
            !isRestrictedByPin(currentVersion, candidateVersion, pinMajorVersion, pinMinorVersion)

    private fun isRestrictedByPin(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ): Boolean {
        // メジャーアップデートのピン留めチェック
        if (pinMajorVersion && candidateVersion.major > currentVersion.major) {
            return true
        }

        // マイナーアップデートのピン留めチェック
        val candidateMinor = candidateVersion.minor ?: 0
        val currentMinor = currentVersion.minor ?: 0
        return pinMinorVersion &&
            candidateVersion.major == currentVersion.major &&
            candidateMinor > currentMinor
    }

    private fun isOlderOrEqual(
        candidate: ArtifactVersion,
        current: ArtifactVersion,
    ): Boolean {
        val comparison = compareVersions(candidate, current)
        return comparison <= 0
    }

    private fun compareVersions(
        candidate: ArtifactVersion,
        current: ArtifactVersion,
    ): Int {
        val diffs = buildVersionDiffs(candidate, current)
        return diffs.firstOrNull { it != 0 } ?: 0
    }

    private fun buildVersionDiffs(
        candidate: ArtifactVersion,
        current: ArtifactVersion,
    ): List<Int> {
        val candMinor = candidate.minor ?: 0
        val curMinor = current.minor ?: 0
        val candPatch = candidate.patch ?: emptyList()
        val curPatch = current.patch ?: emptyList()
        val maxPatchSize = maxOf(candPatch.size, curPatch.size)

        return buildList {
            add(candidate.major - current.major)
            add(candMinor - curMinor)
            for (i in 0 until maxPatchSize) {
                add(candPatch.getOrElse(i) { 0 } - curPatch.getOrElse(i) { 0 })
            }
        }
    }
}
