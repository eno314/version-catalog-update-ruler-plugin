package io.github.eno314.vcu.ruler

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

        val rule = findRule(candidate)
        return isSelectableVersion(candidate, rule) && isUpdatableVersion(candidate, rule)
    }

    private fun findRule(candidate: ModuleVersionCandidate): UpdateRule {
        val libraryName = "${candidate.candidate.group}:${candidate.candidate.module}"
        return extension.libraryRules.findByName(libraryName) ?: extension
    }

    private fun isSelectableVersion(
        moduleVersionCandidate: ModuleVersionCandidate,
        rule: UpdateRule,
    ): Boolean {
        val onlyStable = rule.onlyStable.get()
        val unStableVersionRegex = rule.unStableVersionRegex.get()
        if (!onlyStable ||
            !moduleVersionCandidate.candidate.version.matches(unStableVersionRegex)
        ) {
            return true
        }
        logger.info(
            "{} Skip update because version is not stable. onlyStable:{}, unStableVersionRegex:{}",
            tag,
            onlyStable,
            unStableVersionRegex,
        )
        return false
    }

    private fun isUpdatableVersion(
        moduleVersionCandidate: ModuleVersionCandidate,
        rule: UpdateRule,
    ): Boolean {
        val currentVersion = versionParser.parse(moduleVersionCandidate.currentVersion)
        val candidateVersion = versionParser.parse(moduleVersionCandidate.candidate.version)
        val onlyArtifactVersion = rule.onlyArtifactVersion.get()
        if (currentVersion == null || candidateVersion == null) {
            logger.info(
                "{} Failed version parse to ArtifactVersion. so returns !onlyArtifactVersion setting. : {}",
                tag,
                !onlyArtifactVersion,
            )
            return !onlyArtifactVersion
        }

        return versionUpdateRuler.shouldUpdate(
            currentVersion,
            candidateVersion,
            rule.pinMajorVersion.get(),
            rule.pinMinorVersion.get(),
        )
    }
}
