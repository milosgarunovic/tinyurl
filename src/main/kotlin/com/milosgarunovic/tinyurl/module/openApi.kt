package com.milosgarunovic.tinyurl.module

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*

fun Application.openApi() {
    routing {
        openAPI(path = "openapi")
    }
}