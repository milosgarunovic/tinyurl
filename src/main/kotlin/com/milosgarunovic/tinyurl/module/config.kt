package com.milosgarunovic.tinyurl.module

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.config() {
    install(ContentNegotiation) {
        json()
    }
}