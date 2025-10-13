package io.github.eno314.vcu.ruler

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class VersionCatalogUpdateRulerExtension {
    internal val defaultUnStableVersionRegex =
        Regex(
            ".*(alpha|beta|rc|preview|snapshot|test).*",
            RegexOption.IGNORE_CASE,
        )

    /**
     * Set to true to only consider stable versions as update candidates.
     */
    @get:Input
    abstract val onlyStable: Property<Boolean>

    /**
     * Regular expression for versions that are not considered stable
     */
    @get:Input
    abstract val unStableVersionRegex: Property<Regex>

    /**
     * When set to true, only artifact versions are considered as update candidates.
     */
    @get:Input
    abstract val onlyArtifactVersion: Property<Boolean>

    /**
     * When set to true, major version updates are not allowed
     */
    @get:Input
    abstract val pinMajorVersion: Property<Boolean>

    /**
     * When set to true, minor version updates are not allowed
     */
    @get:Input
    abstract val pinMinorVersion: Property<Boolean>

    init {
        onlyStable.convention(false)
        unStableVersionRegex.convention(defaultUnStableVersionRegex)
        onlyArtifactVersion.convention(false)
        pinMajorVersion.convention(false)
        pinMinorVersion.convention(false)
    }
}
