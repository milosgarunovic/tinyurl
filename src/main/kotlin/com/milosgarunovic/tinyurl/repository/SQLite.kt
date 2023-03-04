package com.milosgarunovic.tinyurl.repository

import org.sqlite.JDBC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object SQLite {

    private lateinit var connection: Connection

    fun setup(dbName: String) {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName.db")
        createDatabase()
    }

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

    fun insert(query: String, vararg parameters: Pair<Int, Any>): Int {
        return update(query, *parameters)
    }

    fun delete(query: String, vararg parameters: Pair<Int, Any>): Int {
        return update(query, *parameters)
    }

    fun update(query: String, vararg parameters: Pair<Int, Any>): Int {
        val prepareStatement = connection.prepareStatement(query)
        for (parameter in parameters) {
            prepareStatement.setObject(parameter.first, parameter.second)
        }
        val executeUpdate = prepareStatement.executeUpdate()
//        prepareStatement.close()
        return executeUpdate
    }

    fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

    private fun createDatabase() {
        val statement = connection.createStatement()

        //language=SQLite
        val createUrlTable = """
        CREATE TABLE IF NOT EXISTS url (
        id                  TEXT PRIMARY KEY NOT NULL,
        shortUrl            TEXT NOT NULL UNIQUE,
        url                 TEXT NOT NULL,
        calculatedExpiry    INTEGER NOT NULL DEFAULT 0,
        dateCreated         INTEGER NOT NULL,
        active              INTEGER NOT NULL DEFAULT 1,
        dateDeactivated     INTEGER NOT NULL DEFAULT 0)"""
        statement.executeUpdate(createUrlTable)

        //language=SQLite
        val createUserTable = """
        CREATE TABLE IF NOT EXISTS users(
        id              TEXT PRIMARY KEY NOT NULL,
        email           TEXT UNIQUE NOT NULL,
        password        TEXT NOT NULL,
        dateCreated     INTEGER NOT NULL,
        active          INTEGER NOT NULL DEFAULT 1,
        dateDeactivated INTEGER NOT NULL DEFAULT 0)"""
        statement.executeUpdate(createUserTable)

        statement.close()
    }

}