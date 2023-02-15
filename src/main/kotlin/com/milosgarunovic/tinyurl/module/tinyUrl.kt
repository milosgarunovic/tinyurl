package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tinyUrl(repository: InMemoryRepository) {
    routing {
        post("/api/tinyUrl") {
            val req = call.receive<TinyUrlAddReq>() // todo must be a valid url
            val res = repository.add(req.url)
            call.respond(HttpStatusCode.Created, res)
        }

        patch("/api/tinyUrl") {
            val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
            repository.update(req.id, req.url)
            call.respond(HttpStatusCode.OK)
        }

        delete("/api/tinyUrl/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                repository.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }

    }
}