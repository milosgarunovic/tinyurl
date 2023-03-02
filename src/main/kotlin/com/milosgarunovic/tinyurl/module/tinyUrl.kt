package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondRedirect
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.repository.UrlRepository
import com.milosgarunovic.tinyurl.repository.UrlRepositorySQLite
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tinyUrl() {

    val repository: UrlRepository = UrlRepositorySQLite()

    routing {

        get("/{path}") {
            val path = call.parameters["path"]!!
            val redirect = call.request.queryParameters["redirect"]?.toBoolean() ?: true
            val url = repository.get(path)?.url
            if (url != null) {
                if (redirect) {
                    call.respondRedirect(url) // TODO add statistics for that url
                } else {
                    call.respond(HttpStatusCode.OK, url)
                }
            } else {
                call.respondStatusCode(HttpStatusCode.NotFound)
            }
        }

        route("/api/tinyUrl") {
            authenticate("auth-basic") {

                post {
                    val req = call.receive<TinyUrlAddReq>() // todo must be a valid url
                    val res = repository.add(req)
                    call.respond(HttpStatusCode.Created, res.shortUrl)
                }

                patch {
                    val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
                    repository.update(req.id, req.url)
                    call.respondStatusCode(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!

                    repository.delete(id)
                    call.respondStatusCode(HttpStatusCode.NoContent)
                }
            }
        }
    }
}