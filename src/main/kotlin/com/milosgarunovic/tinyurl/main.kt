package com.milosgarunovic.tinyurl

import com.milosgarunovic.tinyurl.module.*
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) {

    SQLite.setup("tinyUrl")

    EngineMain.main(args)

}

fun Application.mainModule() {

    config()

    authModule()

    userModule()

    urlModule()

    openApi()
}