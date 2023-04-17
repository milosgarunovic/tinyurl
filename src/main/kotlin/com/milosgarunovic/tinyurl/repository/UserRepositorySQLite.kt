package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User
import java.time.Instant

class UserRepositorySQLite : UserRepository {

    override fun add(user: User): Boolean {
        //language=SQLite
        val query = "INSERT INTO users(id, email, password, date_created) VALUES (?, ?, ?, ?);"
        return SQLite.insert(query, 1 to user.id, 2 to user.email, 3 to user.password, 4 to user.dateCreated)
    }

    override fun getPassword(email: String): String? {
        //language=SQLite
        val query = "SELECT password FROM users WHERE email = ? AND active = 1;"
        return SQLite.query(query, 1 to email) {
            return@query getString(1)
        }
    }

    override fun changePassword(email: String, newPassword: String): Boolean {
        //language=SQLite
        val query = "UPDATE users SET password = ? WHERE email = ?"
        return SQLite.update(query, 1 to newPassword, 2 to email)
    }

    override fun deleteAccount(email: String): Boolean {
        // TODO soft delete of the record, maybe make two implementations for soft and hard. Soft delete is used as a
        //  flag (in our case "active" column/field), and then when reading use active=true. Then hard delete would need
        //  to have either different queries or removed "AND active = 1" from queries and those fields would be
        //  deprecated.
        //language=SQLite
        val query = "UPDATE users SET active = 0, date_deactivated = ? WHERE email = ?"
        return SQLite.update(query, 1 to Instant.now(), 2 to email)
    }

}