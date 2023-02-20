package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.plugin.RequestLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun Application.config() {
    install(RequestLogging)

    authentication {
        basic(name = "auth-basic") {
            validate { credentials ->
                if (credentials.name == "user" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
//            ignoreUnknownKeys = true
//            prettyPrint = true
        })
    }
}