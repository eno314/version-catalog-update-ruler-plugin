package io.github.eno314.vcu.ruler

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class VersionParserTest {
    @InjectMockKs
    private lateinit var versionParser: VersionParser

    @Test
    fun `parse 1_2_3 returns major=1, minor=2, patch=listOf(3)`() {
        assertEquals(
            ArtifactVersion(1, 2, listOf(3)),
            versionParser.parse("1.2.3"),
        )
    }

    @Test
    fun `parse 4_3_2_1-beta returns major=4, minor=3, patch=listOf(2, 1)`() {
        assertEquals(
            ArtifactVersion(4, 3, listOf(2, 1)),
            versionParser.parse("4.3.2.1-beta"),
        )
    }

    @Test
    fun `parse 10_20RC returns major=10, minor=20, patch=null`() {
        assertEquals(
            ArtifactVersion(10, 20, null),
            versionParser.parse("10.20RC"),
        )
    }

    @Test
    fun `parse 5@SNAPSHOT returns major=5, minor=null, patch=null`() {
        assertEquals(
            ArtifactVersion(5, null, null),
            versionParser.parse("5@SNAPSHOT"),
        )
    }

    @Test
    fun `parse 1_a_3 returns major=1, minor=null, patch=null`() {
        assertEquals(
            ArtifactVersion(1, null, null),
            versionParser.parse("1.a.3"),
        )
    }

    @Test
    fun `parse 1_2_3_10 returns major=1, minor=2, patch=listOf(3, 10)`() {
        assertEquals(
            ArtifactVersion(1, 2, listOf(3, 10)),
            versionParser.parse("1.2.3.10"),
        )
    }

    @Test
    fun `parse empty string returns null`() {
        assertNull(versionParser.parse(""))
    }

    @Test
    fun `parse abc returns null`() {
        assertNull(versionParser.parse("abc"))
    }

    @Test
    fun `parse version1_2_3 returns null`() {
        assertNull(versionParser.parse("version1.2.3"))
    }
}
