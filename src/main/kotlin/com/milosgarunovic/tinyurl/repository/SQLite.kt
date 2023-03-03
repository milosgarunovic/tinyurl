package com.milosgarunovic.tinyurl.repository

import org.intellij.lang.annotations.Language
import org.sqlite.JDBC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object SQLite {

    lateinit var connection: Connection

    fun setup(dbName: String) {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName.db")
        createDatabase()
    }

    fun query(query: String, vararg parameters: Pair<Int, Any>): ResultSet {
        val prepareStatement = connection.prepareStatement(query)
        for (parameter in parameters) {
            prepareStatement.setObject(parameter.first, parameter.second)
        }
        return prepareStatement.executeQuery()
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
        return prepareStatement.executeUpdate()
    }

    fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

    private fun createDatabase() {
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

}