package jp.eno314.vcu.ruler

internal data class ArtifactVersion(
    val major: Int,
    val minor: Int?,
    val patch: Double?,
)

internal class VersionParser {
    private val regex = Regex("""(\d+)(?:\.(\d+))?(?:\.(\d+(\.\d)*))?.*""")

    fun parse(version: String): ArtifactVersion? {
        val (majorStr, minorStr, patchStr) = regex.matchEntire(version)?.destructured ?: return null
        return majorStr.toIntOrNull()?.let { major ->
            ArtifactVersion(major, minorStr.toIntOrNull(), patchStr.toDoubleOrNull())
        }
    }
}
