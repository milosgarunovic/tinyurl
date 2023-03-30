package com.milosgarunovic.tinyurl

import com.milosgarunovic.tinyurl.module.config
import com.milosgarunovic.tinyurl.module.openApi
import com.milosgarunovic.tinyurl.module.urlModule
import com.milosgarunovic.tinyurl.module.userModule
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {

    SQLite.setup("tinyUrl")

    embeddedServer(
        CIO,
        port = 8080,
        module = Application::mainModule,
    ).start(wait = true)
}

fun Application.mainModule() {

    config()

    userModule()

    urlModule()

    openApi()
}