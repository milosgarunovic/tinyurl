package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.ErrorWrapper
import com.milosgarunovic.tinyurl.json.LoginReq
import com.milosgarunovic.tinyurl.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.authModule() {

    val authService by inject<AuthService>()

    routing {

        post("/login") {
            val loginReq = call.receive<LoginReq>()

            val res = authService.login(loginReq)

            call.respond(res)
        }

        get("/refreshToken") {
            val authorization = call.request.headers["Authorization"]
            if (authorization == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorWrapper("Expected refresh token in Authorization header."))
                return@get
            }

            val loginRes = authService.refreshToken(authorization.drop("Bearer ".length))
            call.respond(HttpStatusCode.OK, loginRes)
        }
    }
}