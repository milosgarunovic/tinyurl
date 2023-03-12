package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondRedirect
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.service.UrlService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.tinyUrl() {

    val urlService by inject<UrlService>()

    routing {

        get("/{path}") {
            val path = call.parameters["path"]!!
            val redirect = call.request.queryParameters["redirect"]?.toBoolean() ?: true
            // TODO add validation, this can be only 8 chars long
            val url = urlService.getUrl(path)
            if (url != null) {
                if (redirect) {
                    call.respondRedirect(url)
                } else {
                    call.respond(HttpStatusCode.OK, url)
                }
            } else {
                call.respondStatusCode(HttpStatusCode.NotFound)
            }
        }



        route("/api/tinyUrl") {

            authenticate("auth-basic", strategy = AuthenticationStrategy.Optional) {
                // TODO post can be even without authentication
                post {
                    val req = call.receive<TinyUrlAddReq>() // todo must be a valid url
                    val email = call.principal<UserIdPrincipal>()?.name
                    val shortUrl = urlService.add(req, email)
                    call.respond(HttpStatusCode.Created, shortUrl)
                }
            }

            authenticate("auth-basic") {
                patch {
                    val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
                    urlService.update(req.id, req.url)
                    call.respondStatusCode(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!

                    urlService.delete(id)
                    call.respondStatusCode(HttpStatusCode.NoContent)
                }
            }
        }
    }
}