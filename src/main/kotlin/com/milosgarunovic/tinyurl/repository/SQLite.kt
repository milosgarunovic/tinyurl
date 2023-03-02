package com.milosgarunovic.tinyurl.repository

import org.sqlite.JDBC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object SQLite {

    lateinit var connection: Connection

    fun setup(dbName: String) {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName.db")
    }

    fun query(query: String, vararg parameters: Pair<Int, Any>): ResultSet {
        val prepareStatement = connection.prepareStatement(query)
        for (parameter in parameters) {
            prepareStatement.setObject(parameter.first, parameter.second)
        }
        return prepareStatement.executeQuery()
    }

    fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

}