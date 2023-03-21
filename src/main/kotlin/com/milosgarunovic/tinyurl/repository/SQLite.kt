package com.milosgarunovic.tinyurl.repository

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.sqlite.JDBC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object SQLite {

    private lateinit var connection: Connection

    fun setup(dbName: String) {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName.sqlite")
        liquibaseUpdate()
    }

    /**
     * This method is used for tests only.
     */
    fun setupInMemory() {
        Class.forName(JDBC::class.qualifiedName)
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        liquibaseUpdate()
    }

    private fun liquibaseUpdate() {
        val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
        val liquibase = Liquibase("db/sqlite/changelog.xml", ClassLoaderResourceAccessor(), database)
        liquibase.update(Contexts(), LabelExpression())
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
            val updateExecuted = prepareStatement.executeUpdate() == 1
            updateExecuted
        } catch (ex: SQLException) {
            false
        }
    }

    fun close() {
        if (!connection.isClosed) {
            connection.commit()
            connection.close()
        }
    }
}