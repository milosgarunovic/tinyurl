package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.service.PropertiesService
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.propertiesModule() {

    val propertiesService by inject<PropertiesService>()

    val userService by inject<UserService>()

    routing {
        authenticate("jwt") {
            route("/properties") {
                get {
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    if (userService.isAdmin(email)) {
                        call.respond(HttpStatusCode.OK, propertiesService.getProperties())
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }

                post("/enableRegistration") {
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    if (userService.isAdmin(email)) {
                        call.respond(HttpStatusCode.OK, propertiesService.enableRegistration())
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }

                post("/disableRegistration") {
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    if (userService.isAdmin(email)) {
                        call.respond(HttpStatusCode.OK, propertiesService.disableRegistration())
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }

                post("/enablePublicUrlCreation") {
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    if (userService.isAdmin(email)) {
                        call.respond(HttpStatusCode.OK, propertiesService.enablePublicUrlCreation())
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }

                post("/disablePublicUrlCreation") {
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    if (userService.isAdmin(email)) {
                        call.respond(HttpStatusCode.OK, propertiesService.disablePublicUrlCreation())
                    } else {
                        call.respondStatusCode(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}