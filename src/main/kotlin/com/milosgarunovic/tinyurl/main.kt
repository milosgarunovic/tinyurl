package com.milosgarunovic.tinyurl

import com.milosgarunovic.tinyurl.module.config
import com.milosgarunovic.tinyurl.module.openApi
import com.milosgarunovic.tinyurl.module.tinyUrl
import com.milosgarunovic.tinyurl.module.user
import com.milosgarunovic.tinyurl.repository.UserInMemoryRepository
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    embeddedServer(
        CIO,
        port = 8080,
        module = Application::mainModule,
    ).start(wait = true)
}

fun Application.mainModule() {

    val userRepository = UserInMemoryRepository()

    config(userRepository)

    user(userRepository)

    tinyUrl()

    openApi()
}