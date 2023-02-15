package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.plugin.RequestLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.config() {
    install(RequestLogging)

    install(ContentNegotiation) {
        json()
    }
}