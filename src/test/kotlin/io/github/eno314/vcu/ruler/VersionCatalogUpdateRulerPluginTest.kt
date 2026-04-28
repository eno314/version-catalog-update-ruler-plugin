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
        val extension = project.extensions.findByName("versionCatalogUpdateRuler") as? VersionCatalogUpdateRulerExtension
        assertNotNull(extension)

        extension?.apply {
            onlyStable.set(true)
            library("com.example:test") {
                onlyStable.set(false)
            }
        }

        val libraryRule = extension?.libraryRules?.findByName("com.example:test")
        assertNotNull(libraryRule)
        assert(libraryRule?.onlyStable?.get() == false)
        // Check fallback to global
        assert(libraryRule?.pinMajorVersion?.get() == false)

        extension?.pinMajorVersion?.set(true)
        assert(libraryRule?.pinMajorVersion?.get() == true)
    }
}
