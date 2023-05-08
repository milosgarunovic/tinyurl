package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.exception.UnauthorizedException
import com.milosgarunovic.tinyurl.ext.respondRedirect
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.TinyUrlUpdateReq
import com.milosgarunovic.tinyurl.json.UrlAddReq
import com.milosgarunovic.tinyurl.service.PropertiesService
import com.milosgarunovic.tinyurl.service.UrlService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.urlModule() {

    val urlService by inject<UrlService>()

    val propertiesService by inject<PropertiesService>()

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
            if (redirect) {
                call.respondRedirect(url)
            } else {
                call.respond(HttpStatusCode.OK, url)
            }
        }

        route("/api/url") {

            /**
             * Authentication strategy here is optional because anyone can create a short url, even if people don't have
             * an account, the only difference is that they won't have all the functionalities related to links.
             */
            authenticate("jwt", strategy = AuthenticationStrategy.Optional) {
                post {
                    val req = call.receive<UrlAddReq>() // todo must be a valid url
                    val email = call.principal<JWTPrincipal>()?.get("email")
                    if (propertiesService.isPublicUrlCreationEnabled()) { // anyone can create
                        val shortUrl = urlService.add(req, email)
                        call.respond(HttpStatusCode.Created, shortUrl)
                    } else {
                        if (email != null) { // user is required
                            val shortUrl = urlService.add(req, email)
                            call.respond(HttpStatusCode.Created, shortUrl)
                        } else {
                            throw UnauthorizedException()
                        }
                    }
                }
            }

            authenticate("jwt") {
                patch {
                    val req = call.receive<TinyUrlUpdateReq>() // todo must be a valid url
                    urlService.update(req.id, req.url, call.principal<JWTPrincipal>()?.get("email")!!)
                    call.respondStatusCode(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!
                    urlService.delete(id, call.principal<JWTPrincipal>()?.get("email")!!)
                    call.respondStatusCode(HttpStatusCode.NoContent)
                }
            }
        }
    }
}