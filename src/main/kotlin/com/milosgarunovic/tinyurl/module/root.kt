package com.milosgarunovic.tinyurl.module

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.root() {
    routing {
        get("/{path}") {
            val path = call.parameters["path"]
            // if path exists -> 301 Moved Permanently or 303 See Other
            if (path == "true") {
                call.respondRedirect("https://google.com", true)
            } else {
                // else 404 Not Found
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}