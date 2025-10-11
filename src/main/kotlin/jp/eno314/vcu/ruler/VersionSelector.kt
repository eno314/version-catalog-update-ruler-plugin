package jp.eno314.vcu.ruler

import nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate
import nl.littlerobots.vcu.plugin.resolver.ModuleVersionSelector
import org.gradle.api.logging.Logger

internal class VersionSelector(
    private val extension: VersionCatalogUpdateRulerExtension,
    private val versionParser: VersionParser,
    private val versionUpdateRuler: VersionUpdateRuler,
    private val logger: Logger,
) : ModuleVersionSelector {
    private val tag = "[VersionCatalogUpdateRulerPlugin]"

    override fun select(candidate: ModuleVersionCandidate): Boolean {
        logger.info(
            "{} candidate:{} current:{}",
            tag,
            candidate.candidate.displayName,
            candidate.currentVersion,
        )

        return isSelectableVersion(candidate) && isUpdatableVersion(candidate)
    }

    private fun isSelectableVersion(moduleVersionCandidate: ModuleVersionCandidate): Boolean {
        if (!extension.onlyStable.get() ||
            !moduleVersionCandidate.candidate.version.matches(extension.unStableVersionRegex.get())
        ) {
            return true
        }
        logger.info(
            "{} Skip update because version is not stable. onlyStable:{}, unStableVersionRegex:{}",
            tag,
            extension.onlyStable.get(),
            extension.unStableVersionRegex.get(),
        )
        return false
    }

    private fun isUpdatableVersion(moduleVersionCandidate: ModuleVersionCandidate): Boolean {
        val currentVersion = versionParser.parse(moduleVersionCandidate.currentVersion)
        val candidateVersion = versionParser.parse(moduleVersionCandidate.candidate.version)
        if (currentVersion == null || candidateVersion == null) {
            logger.info(
                "{} Failed version parse to ArtifactVersion. so returns !onlyArtifactVersion setting. : {}",
                tag,
                !extension.onlyArtifactVersion.get(),
            )
            return !extension.onlyArtifactVersion.get()
        }

        return versionUpdateRuler.shouldUpdate(
            currentVersion,
            candidateVersion,
            extension.pinMajorVersion.get(),
            extension.pinMinorVersion.get(),
        )
    }
}
