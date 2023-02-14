package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.root(repository: InMemoryRepository) {
    routing {
        get("/{path}") {
            val path = call.parameters["path"]
            // if path exists -> 301 Moved Permanently or 303 See Other

            if (path != null) {
                val url = repository.get(path);
                if (url != null) {
                    // add statistics for that url
                    call.respondRedirect(url, true)
                } else {
                    // else 404 Not Found
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}