package io.github.eno314.vcu.ruler

internal class VersionUpdateRuler {
    fun shouldUpdate(
        currentVersion: ArtifactVersion,
        candidateVersion: ArtifactVersion,
        pinMajorVersion: Boolean,
        pinMinorVersion: Boolean,
    ): Boolean {
        // 1. 現在のバージョン以下なら常に許可（Gradleが "could not be resolved" で落ちるのを防ぐため）
        if (isOlderOrEqual(candidateVersion, currentVersion)) return true

        // 2. メジャーアップデートのピン留めチェック
        if (pinMajorVersion && candidateVersion.major > currentVersion.major) {
            return false
        }

        // 3. マイナーアップデートのピン留めチェック
        if (pinMinorVersion && candidateVersion.major == currentVersion.major) {
            val candidateMinor = candidateVersion.minor ?: 0
            val currentMinor = currentVersion.minor ?: 0
            if (candidateMinor > currentMinor) {
                return false
            }
        }

        // 制限に引っかからなかった新しいバージョンを許可
        return true
    }

    private fun isOlderOrEqual(candidate: ArtifactVersion, current: ArtifactVersion): Boolean {
        // メジャーが違えば、小さい方が古い
        if (candidate.major != current.major) return candidate.major < current.major

        // メジャーが同じなら、マイナーを比較
        val candMinor = candidate.minor ?: 0
        val curMinor = current.minor ?: 0
        if (candMinor != curMinor) return candMinor < curMinor

        // マイナーも同じなら、パッチのListを先頭から順番に比較
        val candPatch = candidate.patch ?: emptyList()
        val curPatch = current.patch ?: emptyList()
        val maxSize = maxOf(candPatch.size, curPatch.size)

        for (i in 0 until maxSize) {
            val c = candPatch.getOrElse(i) { 0 }
            val cur = curPatch.getOrElse(i) { 0 }
            if (c != cur) return c < cur
        }

        // 全く同じバージョン
        return true
    }
}
