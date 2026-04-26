package io.github.eno314.vcu.ruler

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

interface UpdateRule {
    /**
     * Set to true to only consider stable versions as update candidates.
     */
    @get:Input
    val onlyStable: Property<Boolean>

    /**
     * Regular expression for versions that are not considered stable
     */
    @get:Input
    val unStableVersionRegex: Property<Regex>

    /**
     * When set to true, only artifact versions are considered as update candidates.
     */
    @get:Input
    val onlyArtifactVersion: Property<Boolean>

    /**
     * When set to true, major version updates are not allowed
     */
    @get:Input
    val pinMajorVersion: Property<Boolean>

    /**
     * When set to true, minor version updates are not allowed
     */
    @get:Input
    val pinMinorVersion: Property<Boolean>
}

abstract class LibraryUpdateRule
    @Inject
    constructor(
        private val name: String,
    ) : UpdateRule,
        Named {
        @Input
        override fun getName(): String = name
    }

abstract class VersionCatalogUpdateRulerExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) : UpdateRule {
        internal val defaultUnStableVersionRegex =
            Regex(
                ".*(alpha|beta|rc|preview|snapshot|test).*",
                RegexOption.IGNORE_CASE,
            )

        /**
         * Library specific update rules.
         */
        @get:Input
        val libraryRules: NamedDomainObjectContainer<LibraryUpdateRule> =
            objects.domainObjectContainer(LibraryUpdateRule::class.java)

        fun library(
            name: String,
            action: Action<LibraryUpdateRule>,
        ) {
            libraryRules.register(name, action)
        }

        init {
            onlyStable.convention(false)
            unStableVersionRegex.convention(defaultUnStableVersionRegex)
            onlyArtifactVersion.convention(false)
            pinMajorVersion.convention(false)
            pinMinorVersion.convention(false)

            libraryRules.all {
                onlyStable.convention(this@VersionCatalogUpdateRulerExtension.onlyStable)
                unStableVersionRegex.convention(this@VersionCatalogUpdateRulerExtension.unStableVersionRegex)
                onlyArtifactVersion.convention(this@VersionCatalogUpdateRulerExtension.onlyArtifactVersion)
                pinMajorVersion.convention(this@VersionCatalogUpdateRulerExtension.pinMajorVersion)
                pinMinorVersion.convention(this@VersionCatalogUpdateRulerExtension.pinMinorVersion)
            }
        }
    }
