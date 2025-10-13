package io.github.eno314.vcu.ruler

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class VersionCatalogUpdateRulerPluginTest {
    @Test
    fun `plugin is applied correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.eno314.version-catalog-update-ruler")

        assertNotNull(project.plugins.findPlugin("nl.littlerobots.version-catalog-update"))
        assertNotNull(project.extensions.findByName("versionCatalogUpdateRuler"))
    }
}
