package io.github.eno314.vcu.ruler

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VersionCatalogUpdateRulerExtensionTest {
    @Test
    fun `defaultUnStableVersionRegex matches unstable versions`() {
        val project = ProjectBuilder.builder().build()
        val extension = project.objects.newInstance(VersionCatalogUpdateRulerExtension::class.java)
        val regex = extension.defaultUnStableVersionRegex

        assertTrue("1.0.0-alpha".matches(regex))
        assertTrue("1.0.0-beta".matches(regex))
        assertTrue("1.0.0-rc1".matches(regex))
        assertTrue("1.0.0-preview".matches(regex))
        assertTrue("1.0.0-SNAPSHOT".matches(regex))
        assertTrue("1.0.0-test".matches(regex))

        // Milestone versions
        assertTrue("1.0.0-M1".matches(regex))
        assertTrue("1.0.0-M4".matches(regex))
        assertTrue("1.0.0-M12".matches(regex))
    }

    @Test
    fun `defaultUnStableVersionRegex does not match stable versions`() {
        val project = ProjectBuilder.builder().build()
        val extension = project.objects.newInstance(VersionCatalogUpdateRulerExtension::class.java)
        val regex = extension.defaultUnStableVersionRegex

        assertFalse("1.0.0".matches(regex))
        assertFalse("1.2.3-final".matches(regex))
        assertFalse("2.1.0-RELEASE".matches(regex))
        assertFalse("1.0.0.GA".matches(regex))
    }
}
