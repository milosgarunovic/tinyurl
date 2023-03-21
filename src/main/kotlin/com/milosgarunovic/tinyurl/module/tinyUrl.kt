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
            if (path.length != 8) { // 8 is generated in UrlPathGenerator.kt
                call.respondStatusCode(HttpStatusCode.NotFound)
                return@get
            }

            val redirect = call.request.queryParameters["redirect"]?.toBoolean() ?: true
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
                    val email = call.principal<UserIdPrincipal>()?.name!!
                    urlService.update(req.id, req.url, email)
                    call.respondStatusCode(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!
                    val email = call.principal<UserIdPrincipal>()?.name!!
                    if (urlService.delete(id, email)) {
                        call.respondStatusCode(HttpStatusCode.NoContent)
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}