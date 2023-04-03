package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.LoginReq
import com.milosgarunovic.tinyurl.service.LoginService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.loginModule() {

    val loginService by inject<LoginService>()

    routing {

        post("/login") {
            val loginReq = call.receive<LoginReq>()

            val res = loginService.login(loginReq)

            call.respond(res)
        }
    }
}