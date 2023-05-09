plugins {
    val kotlinVersion = "1.8.20"

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

val ktorVersion = "2.3.0"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:3.3.1")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.1")

    implementation("org.liquibase:liquibase-core:4.20.0")

    implementation("com.password4j:password4j:1.7.0")

    implementation("ch.qos.logback:logback-classic:1.4.6")

    implementation("commons-lang:commons-lang:2.6")

    implementation("org.xerial:sqlite-jdbc:3.41.0.0")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        // junit 4 excluded
        exclude(group = "junit", module = "junit")
    }
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