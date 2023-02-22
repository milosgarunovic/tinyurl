plugins {
    val kotlinVersion = "1.8.10"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.dokka") version "1.7.20"
    application // Apply the application plugin to add support for building a CLI application in Java.
}

group = "com.milosgarunovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.2.3"
val kotlinVersion = "1.8.10"
val mockkVersion = "1.13.4"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("commons-lang:commons-lang:2.6")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.milosgarunovic.tinyurl.MainKt")
}

// configuration for Kover to exclude some classes/packages
kover {
    filters {
        classes {
            excludes += listOf(
                "com.milosgarunovic.tinyurl.MainKt", // main doesn't make sense to be tested
                "com.milosgarunovic.tinyurl.json.*", // json doesn't make sense to be tested
            )
        }
    }
}