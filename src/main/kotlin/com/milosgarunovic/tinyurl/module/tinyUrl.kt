package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.TinyUrlAdd
import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tinyUrl(repository: InMemoryRepository) {
    routing {
        post("/api/tinyUrl") {
            val req = call.receive<TinyUrlAdd>() // todo must be a valid url
            val res = repository.add(req.actualUrl)
            call.respond(HttpStatusCode.Created, res)
        }

        delete("/api/tinyUrl/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                repository.delete(id)
            }
        }

    }
}