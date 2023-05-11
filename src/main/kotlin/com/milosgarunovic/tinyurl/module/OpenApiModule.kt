package com.milosgarunovic.tinyurl.module

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.openApiModule() {
    routing {
        swaggerUI(path = "swagger")
    }
}