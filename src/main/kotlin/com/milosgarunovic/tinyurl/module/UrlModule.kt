package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondRedirect
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.json.UrlAddReq
import com.milosgarunovic.tinyurl.service.UrlService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.urlModule() {

    val urlService by inject<UrlService>()

    routing {

        get("/{path}") {
            val path = call.parameters["path"]!!
            if (path.length != 8) { // 8 is generated in UrlPathGenerator.kt TODO maybe move this in a configuration file
                call.respondStatusCode(HttpStatusCode.NotFound)
                return@get
            }

            val redirect = call.request.queryParameters["redirect"]?.toBoolean() ?: true
            // TODO queryParameter origin or source or something like that so we can group from which website this url
            //   was called
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

        route("/api/url") {

            authenticate("auth-basic", strategy = AuthenticationStrategy.Optional) {
                post {
                    val req = call.receive<UrlAddReq>() // todo must be a valid url
                    val email = call.principal<UserIdPrincipal>()?.name
                    val shortUrl = urlService.add(req, email)
                    call.respond(HttpStatusCode.Created, shortUrl)
                }
            }

            authenticate("auth-basic") {
                patch {
                    val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
                    if (urlService.update(req.id, req.url, call.principal<UserIdPrincipal>()?.name!!)) {
                        call.respondStatusCode(HttpStatusCode.OK)
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!
                    if (urlService.delete(id, call.principal<UserIdPrincipal>()?.name!!)) {
                        call.respondStatusCode(HttpStatusCode.NoContent)
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}