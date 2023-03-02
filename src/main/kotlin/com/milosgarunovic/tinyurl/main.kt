package com.milosgarunovic.tinyurl

import com.milosgarunovic.tinyurl.module.config
import com.milosgarunovic.tinyurl.module.openApi
import com.milosgarunovic.tinyurl.module.tinyUrl
import com.milosgarunovic.tinyurl.module.user
import com.milosgarunovic.tinyurl.repository.SQLite
import com.milosgarunovic.tinyurl.repository.UserInMemoryRepository
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.intellij.lang.annotations.Language

fun main() {

    SQLite.setup("tinyUrl")
    createDatabase()

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

fun createDatabase() {
    val statement = SQLite.connection.createStatement()

    @Language("SQLite")
    val query = """
        CREATE TABLE IF NOT EXISTS url (
        id TEXT PRIMARY KEY NOT NULL,
        shortUrl TEXT NOT NULL,
        url TEXT NOT NULL,
        calculatedExpiry INTEGER,
        dateCreated INTEGER NOT NULL,
        active INTEGER NOT NULL,
        dateDeactivated INTEGER)"""
    statement.executeUpdate(query)
    statement.close()
}