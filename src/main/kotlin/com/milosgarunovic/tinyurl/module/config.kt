package com.milosgarunovic.tinyurl.module

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.milosgarunovic.tinyurl.exception.BadRequestException
import com.milosgarunovic.tinyurl.exception.ConflictException
import com.milosgarunovic.tinyurl.exception.NotFoundException
import com.milosgarunovic.tinyurl.exception.UnauthorizedException
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.plugin.RequestLogging
import com.milosgarunovic.tinyurl.repository.*
import com.milosgarunovic.tinyurl.service.LoginService
import com.milosgarunovic.tinyurl.service.PasswordService
import com.milosgarunovic.tinyurl.service.UrlService
import com.milosgarunovic.tinyurl.service.UserService
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

@OptIn(ExperimentalSerializationApi::class)
fun Application.config() {
    install(RequestLogging)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error(cause)
            call.respondStatusCode(HttpStatusCode.InternalServerError)
        }

        exception<NotFoundException> { call, cause -> call.respond(cause.statusCode, cause.message) }
        exception<BadRequestException> { call, cause -> call.respond(cause.statusCode, cause.message) }
        exception<ConflictException> { call, cause -> call.respond(cause.statusCode, cause.message) }
        exception<UnauthorizedException> { call, cause -> call.respond(cause.statusCode, cause.message) }
    }

    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<UrlRepository> { UrlRepositorySQLite() }
                single<UserRepository> { UserRepositorySQLite() }
                single<UrlStatisticsRepository> { UrlStatisticsRepositorySQLIte() }

                singleOf(::UrlService)
                singleOf(::UserService)
                singleOf(::PasswordService)
                singleOf(::LoginService)
            }
        )
    }

    authentication {
        jwt(name = "jwt") {
            // defines a function
            verifier(
                JWT.require(Algorithm.HMAC256("483f1296-30d0-41df-8376-120fc793d9eb"))
//                .withAudience(audience)
//                .withIssuer(issuer)
                    .build()
            )

            // validate fields in payload if necessary and create JWTPrincipal
            validate { credentials ->
                if (credentials.payload.getClaim("exp").asLong() < InstantUtil.now().toEpochMilli()) {
                    JWTPrincipal(credentials.payload)
                } else {
                    throw UnauthorizedException()
                }
            }

            // if authentication fails, this would be the response
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
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