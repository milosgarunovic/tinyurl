package com.milosgarunovic.tinyurl.module

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.milosgarunovic.tinyurl.json.LoginReq
import com.milosgarunovic.tinyurl.service.LoginService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.minutes

fun Application.loginModule() {

    val loginService by inject<LoginService>()

    routing {

        post("/login") {
            val loginReq = call.receive<LoginReq>()

            loginService.validate(loginReq)

            // secret and expiry are temporary, should be stored in config file
            val secret = "483f1296-30d0-41df-8376-120fc793d9eb"
            val expiryMinutes = 3

            val token = JWT.create()
//                .withAudience(audience)
//                .withIssuer(issuer)
                .withClaim("email", loginReq.email)
                .withExpiresAt(Date(Instant.now().plusMillis(expiryMinutes.minutes.inWholeMilliseconds).toEpochMilli()))
                .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
        }
    }
}