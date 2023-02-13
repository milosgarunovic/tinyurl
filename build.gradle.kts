plugins {
    kotlin("jvm") version "1.8.10"
    application // Apply the application plugin to add support for building a CLI application in Java.
}

group = "com.milosgarunovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.2.3")
    implementation("io.ktor:ktor-server-cio:2.2.3")

    implementation("org.slf4j:slf4j-nop:2.0.6") // https://www.slf4j.org/codes.html#StaticLoggerBinder

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.milosgarunovic.tinyurl.Main.kt")
}