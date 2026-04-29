package io.github.eno314.vcu.ruler

internal data class ArtifactVersion(
    val major: Int,
    val minor: Int?,
    val patch: List<Int>?,
)

internal class VersionParser {
    fun parse(version: String): ArtifactVersion? {
        // Strip any non-numeric suffix (e.g. "-beta", "RC", "@SNAPSHOT")
        val numericPart = version.takeWhile { it.isDigit() || it == '.' }.trimEnd('.')
        val parts = numericPart.split(".")
        val major = parts[0].toIntOrNull() ?: return null

        return ArtifactVersion(
            major = major,
            minor = parts.getOrNull(1)?.toIntOrNull(),
            patch = parsePatch(parts),
        )
    }

    private fun parsePatch(parts: List<String>): List<Int>? {
        if (parts.size < MINIMUM_PARTS_FOR_PATCH) return null
        val patchParts = parts.drop(2).map { it.toIntOrNull() }
        return if (patchParts.any { it == null }) null else patchParts.filterNotNull()
    }

    companion object {
        private const val MINIMUM_PARTS_FOR_PATCH = 3
    }
}
