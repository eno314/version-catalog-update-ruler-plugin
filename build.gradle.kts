plugins {
    `kotlin-dsl`
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

group = "jp.eno314.vcu.ruler.plugin"
version = "1.0.0-SNAPSHOT"

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
    plugins {
        create("versionCatalogUpdateRuler") {
            id = "jp.eno314.version-catalog-update-ruler"
            implementationClass = "jp.eno314.vcu.ruler.VersionCatalogUpdateRulerPlugin"
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
