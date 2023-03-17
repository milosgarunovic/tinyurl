package com.milosgarunovic.tinyurl.repository

import org.sqlite.JDBC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object SQLite {

    private lateinit var connection: Connection

    fun setup(dbName: String) {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName.db")
        createDatabase()
    }

    /**
     * This method is used for tests only.
     */
    fun setupInMemory() {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        createDatabase()
    }

    fun query(query: String, vararg parameters: Pair<Int, Any>): ResultSet {
        val prepareStatement = connection.prepareStatement(query)
        for (parameter in parameters) {
            prepareStatement.setObject(parameter.first, parameter.second)
        }
        val executeQuery = prepareStatement.executeQuery()
//        prepareStatement.close()
        return executeQuery
    }

    fun insert(query: String, vararg parameters: Pair<Int, Any>): Boolean {
        return update(query, *parameters)
    }

    fun update(query: String, vararg parameters: Pair<Int, Any>): Boolean {
        val prepareStatement = connection.prepareStatement(query)
        for (parameter in parameters) {
            prepareStatement.setObject(parameter.first, parameter.second)
        }
        return try {
            prepareStatement.executeUpdate()
            true
        } catch (ex: SQLException) {
            false
        }
    }

    fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

    private fun createDatabase() {
        val statement = connection.createStatement()

        //language=SQLite
        val createUserTable = """
        CREATE TABLE IF NOT EXISTS users(
        id              TEXT PRIMARY KEY NOT NULL,
        email           TEXT UNIQUE NOT NULL,
        password        TEXT NOT NULL,
        date_created     INTEGER NOT NULL,
        active          INTEGER NOT NULL DEFAULT 1,
        date_deactivated INTEGER NOT NULL DEFAULT 0)"""
        statement.executeUpdate(createUserTable)

        //language=SQLite
        val createUrlTable = """
        CREATE TABLE IF NOT EXISTS url (
        id                  TEXT PRIMARY KEY NOT NULL,
        short_url           TEXT NOT NULL UNIQUE,
        url                 TEXT NOT NULL,
        calculated_expiry   INTEGER NOT NULL DEFAULT 0,
        date_created        INTEGER NOT NULL,
        active              INTEGER NOT NULL DEFAULT 1,
        date_deactivated    INTEGER NOT NULL DEFAULT 0,
        user_id             TEXT,
        FOREIGN KEY (user_id) REFERENCES users(id) )"""
        statement.executeUpdate(createUrlTable)

        statement.close()
    }

}