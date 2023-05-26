plugins {
    val kotlinVersion = "1.8.20"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("io.ktor.plugin") version "2.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.dokka") version "1.7.20"
    application // Apply the application plugin to add support for building a CLI application in Java.
    id("org.jreleaser.jdks") version "1.6.0" // downloads JDK necessary to run the app
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
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:3.3.1")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.1")

    implementation("org.liquibase:liquibase-core:4.21.1")

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
    // k2 compiler
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

application {
    mainClass.set("com.milosgarunovic.tinyurl.main")
}

ktor {
    fatJar {
        archiveFileName.set("tinyurl-fat.jar")
    }
}

// configuration for Kover to exclude some classes/packages
kover {
    filters {
        classes {
            excludes += listOf(
                "com.milosgarunovic.tinyurl.main", // main doesn't make sense to be tested
                "com.milosgarunovic.tinyurl.json.*", // json doesn't make sense to be tested
            )
        }
    }
}

jdks {
    create("corretto17Linux") {
        platform.set("linux")
        url.set("https://corretto.aws/downloads/latest/amazon-corretto-17-x64-linux-jdk.deb")
        checksum.set("8fdc8cc490d4ffc8d37a0fd81431b5d9de2bcbbafc4effa6b00b7fa6a7ce453c")
    }
    create("corretto17Windows") {
        platform.set("windows")
        url.set("https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi")
        checksum.set("049f15b909ea66ee9ba9cd6396119a0c78c73e3d10d962d4e16fb0d296c160cd")
    }
    create("corretto17Mac") {
        platform.set("mac")
        url.set("https://corretto.aws/downloads/latest/amazon-corretto-17-x64-macos-jdk.pkg")
        checksum.set("8af9245c4a7893960e942dfbf40cb12e18dcfc16eae8cc9c92f544d775fb8150")
    }
}