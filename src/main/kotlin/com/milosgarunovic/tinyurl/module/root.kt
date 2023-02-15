package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import com.milosgarunovic.tinyurl.util.respondRedirect
import com.milosgarunovic.tinyurl.util.respondWithStatusCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.root(repository: InMemoryRepository) {
    routing {
        get("/{path}") {
            val path = call.parameters["path"]
            if (path != null) {
                val url = repository.get(path);
                if (url != null) {
                    call.respondRedirect(url) // TODO add statistics for that url
                } else {
                    call.respondWithStatusCode(HttpStatusCode.NotFound)
                }
            }
        }
    }
}