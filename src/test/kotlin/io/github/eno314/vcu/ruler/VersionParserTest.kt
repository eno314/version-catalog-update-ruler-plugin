package io.github.eno314.vcu.ruler

import io.github.eno314.vcu.ruler.ArtifactVersion
import io.github.eno314.vcu.ruler.VersionParser
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class VersionParserTest {
    @InjectMockKs
    private lateinit var versionParser: VersionParser

    @ParameterizedTest
    @CsvSource(
        value = [
            "1.2.3, 1, 2, 3.0",
            "4.3.2.1-beta, 4, 3, 2.1",
            "10.20RC, 10, 20, ",
            "5@SNAPSHOT, 5, , ",
            "1.a.3, 1, , ",
        ],
    )
    fun `parse returns ArtifactVersion when version string matches pattern`(
        version: String,
        major: Int,
        minor: Int?,
        patch: Double?,
    ) {
        assertEquals(
            ArtifactVersion(major, minor, patch),
            versionParser.parse(version),
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "abc", "version1.2.3"])
    fun `parse returns null when version string does not match pattern`(version: String) {
        assertNull(versionParser.parse(version))
    }
}
