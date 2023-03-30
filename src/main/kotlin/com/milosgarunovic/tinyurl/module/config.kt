package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.exception.BadRequestException
import com.milosgarunovic.tinyurl.exception.ConflictException
import com.milosgarunovic.tinyurl.exception.NotFoundException
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.plugin.RequestLogging
import com.milosgarunovic.tinyurl.repository.*
import com.milosgarunovic.tinyurl.service.PasswordService
import com.milosgarunovic.tinyurl.service.UrlService
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
            }
        )
    }

    // order of creating this dependency is important, it needs to be after install(Koin)
    val userService by inject<UserService>()
    authentication {
        basic(name = "auth-basic") {
            validate { credentials ->
                if (userService.validate(credentials.name, credentials.password)) {
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