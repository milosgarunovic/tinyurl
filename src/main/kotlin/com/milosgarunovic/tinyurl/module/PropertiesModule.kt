package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.service.PropertiesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.propertiesModule() {

    val propertiesService by inject<PropertiesService>()

    routing {
        authenticate("jwt-admin") {
            route("/api/properties") {
                get {
                    call.respond(HttpStatusCode.OK, propertiesService.getProperties())
                }

                post("/enableRegistration") {
                    call.respond(HttpStatusCode.OK, propertiesService.enableRegistration())
                }

                post("/disableRegistration") {
                    call.respond(HttpStatusCode.OK, propertiesService.disableRegistration())
                }

                post("/enablePublicUrlCreation") {
                    call.respond(HttpStatusCode.OK, propertiesService.enablePublicUrlCreation())
                }

                post("/disablePublicUrlCreation") {
                    call.respond(HttpStatusCode.OK, propertiesService.disablePublicUrlCreation())
                }
            }
        }
    }
}