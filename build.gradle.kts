plugins {
    val kotlinVersion = "2.0.0-Beta1"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlinx.kover") version "0.7.4"
    id("org.jetbrains.dokka") version "1.9.10"
    id("com.github.ben-manes.versions") version "0.50.0"
    application // Apply the application plugin to add support for building a CLI application in Java.
}

group = "com.milosgarunovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion by extra { "2.3.6" }

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:3.5.2-RC1")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.2-RC1")

    implementation("org.liquibase:liquibase-core:4.25.0")

    implementation("com.password4j:password4j:1.7.3")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation("commons-lang:commons-lang:2.6")

    implementation("org.xerial:sqlite-jdbc:3.44.0.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.0.CR1")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.4.0.CR1")

    testImplementation(kotlin("test", "2.0.0-Beta1"))
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        // junit 4 excluded
        exclude(group = "junit", module = "junit")
    }

    // enforce versions on transitive dependencies
    constraints {
        implementation("org.jetbrains.intellij.deps", "intellij-coverage-reporter") {
            version { strictly("1.0.740") }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AMAZON)
    }

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
    excludeInstrumentation {
        classes(
            "com.milosgarunovic.tinyurl.main", // main doesn't make sense to be tested
        )
        packages(
            "com.milosgarunovic.tinyurl.json.*", // json doesn't make sense to be tested
        )
    }
}