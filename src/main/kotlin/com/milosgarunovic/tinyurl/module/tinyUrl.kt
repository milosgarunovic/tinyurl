package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import com.milosgarunovic.tinyurl.util.respondWithStatusCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tinyUrl(repository: InMemoryRepository) {
    routing {

        route("/api/tinyUrl") {

        post {
                val req = call.receive<TinyUrlAddReq>() // todo must be a valid url
                val res = repository.add(req.url)
                call.respond(HttpStatusCode.Created, res)
            }

            patch {
                val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
                repository.update(req.id, req.url)
                call.respondWithStatusCode(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

                repository.delete(id)
                call.respondWithStatusCode(HttpStatusCode.NoContent)
            }
        }
    }
}