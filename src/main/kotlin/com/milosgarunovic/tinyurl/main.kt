package com.milosgarunovic.tinyurl

import com.milosgarunovic.tinyurl.module.*
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {

    SQLite.setup("tinyUrl")

    embeddedServer(
        CIO,
        port = 8080, // TODO move to config
        module = Application::mainModule,
    ).start(wait = true)
}

fun Application.mainModule() {

    config()

    authModule()

    userModule()

    urlModule()

    openApi()
}