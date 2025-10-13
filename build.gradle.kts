plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.gradle.plugin.publish)
}

group = "io.github.eno314.vcu.ruler.plugin"
version = providers.gradleProperty("version").getOrElse("0.0.1-SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.version.catalog.update)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
}

gradlePlugin {
    website = "https://github.com/eno314/version-catalog-update-ruler-plugin"
    vcsUrl = "https://github.com/eno314/version-catalog-update-ruler-plugin.git"

    plugins {
        create("versionCatalogUpdateRuler") {
            id = "io.github.eno314.version-catalog-update-ruler"
            displayName = "Version Catalog Update Ruler Plugin"
            description =
                "A Gradle plugin that provides a custom version selector for the Version Catalog Update plugin."
            tags = listOf("version-catalog", "dependency-management", "versioning", "updates")
            implementationClass = "io.github.eno314.vcu.ruler.VersionCatalogUpdateRulerPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

kotlin {
    jvmToolchain(21)
}

detekt {
    source.setFrom(
        files(
            "src/main/kotlin",
        ),
    )
}

publishing {
    repositories {
        maven {
            name = "myLocalRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
