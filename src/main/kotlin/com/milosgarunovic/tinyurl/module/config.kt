package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.plugin.RequestLogging
import com.milosgarunovic.tinyurl.repository.UrlRepository
import com.milosgarunovic.tinyurl.repository.UrlRepositorySQLite
import com.milosgarunovic.tinyurl.repository.UserRepository
import com.milosgarunovic.tinyurl.repository.UserRepositorySQLite
import com.milosgarunovic.tinyurl.service.UrlService
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<UrlRepository> { UrlRepositorySQLite() }
                single<UserRepository> { UserRepositorySQLite() }

                singleOf(::UrlService)
                singleOf(::UserService)
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