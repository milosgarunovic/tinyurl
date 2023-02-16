plugins {
    val kotlinVersion = "1.8.10"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    application // Apply the application plugin to add support for building a CLI application in Java.
}

group = "com.milosgarunovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.2.3"
val kotlinVersion = "1.8.10"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("commons-lang:commons-lang:2.6")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

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