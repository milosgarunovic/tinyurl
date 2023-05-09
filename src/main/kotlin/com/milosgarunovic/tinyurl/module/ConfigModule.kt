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
import com.milosgarunovic.tinyurl.service.*
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

@OptIn(ExperimentalSerializationApi::class)
fun Application.configModule() {

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
                single<PropertiesRepository> { PropertiesRepositorySQLite() }

                // helper singletons to have access in KoinComponent tagged classes and not just in ktor modules
                single<ApplicationConfig> { environment.config }
                single<Logger> { environment.log }

                singleOf(::UrlService)
                singleOf(::UserService)
                singleOf(::PasswordService)
                singleOf(::AuthService)
                singleOf(::PropertiesService)
            }
        )
    }

    val accessTokenSecret = environment.config.property("jwt.accessTokenSecret").getString()
    val userService by inject<UserService>()
    authentication {
        jwt(name = "jwt") {
            verifier(JWT.require(Algorithm.HMAC256(accessTokenSecret)).build())

            // validate fields in payload if necessary and create JWTPrincipal
            validate { credentials ->
                // if expiration is in the future
                if (credentials.payload.getClaim("exp").asLong() > InstantUtil.now().toEpochMilli()) {
                    JWTPrincipal(credentials.payload)
                } else {
                    null
                }
            }

            // if authentication fails, this would be the response
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

        jwt(name = "jwt-admin") {
            verifier(JWT.require(Algorithm.HMAC256(accessTokenSecret)).build())

            validate { credentials ->
                // TODO figure out why there is ""test@email.com"" like it's a string with "
                //  so we need removeSurrounding
                val payload = credentials.payload
                if (payload.getClaim("exp").asLong() > InstantUtil.now().toEpochMilli()
                    && userService.isAdmin(payload.getClaim("email").toString().removeSurrounding("\""))
                ) {
                    JWTPrincipal(payload)
                } else {
                    null
                }
            }

            challenge { _, _ -> call.respondStatusCode(HttpStatusCode.Forbidden) }
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