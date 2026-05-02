package io.github.eno314.vcu.ruler

import nl.littlerobots.vcu.plugin.VersionCatalogUpdateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionCatalogUpdateRulerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create(
                "versionCatalogUpdateRuler",
                VersionCatalogUpdateRulerExtension::class.java,
            )

        project.pluginManager.apply("nl.littlerobots.version-catalog-update")

        project.extensions.configure(VersionCatalogUpdateExtension::class.java) {
            versionSelector(
                VersionSelector(
                    extension,
                    VersionParser(),
                    VersionCandidateValidator(),
                    project.logger,
                ),
            )
        }
    }
}
