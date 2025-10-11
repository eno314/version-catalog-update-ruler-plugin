package jp.eno314.vcu.ruler

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

        project.plugins.apply("nl.littlerobots.version-catalog-update")

        val baseExtension = project.extensions.getByType(VersionCatalogUpdateExtension::class.java)
        baseExtension.versionSelector(
            VersionSelector(
                extension,
                VersionParser(),
                VersionUpdateRuler(),
                project.logger,
            ),
        )
    }
}
