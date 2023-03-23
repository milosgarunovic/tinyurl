package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User

class UserRepositorySQLite : UserRepository {

    override fun add(user: User): Boolean {
        //language=SQLite
        val query = "INSERT INTO users(id, email, password, date_created) VALUES (?, ?, ?, ?);"
        val dateCreated = user.dateCreated.toEpochMilli()
        return SQLite.insert(query, 1 to user.id, 2 to user.email, 3 to user.password, 4 to dateCreated)
    }

    override fun validate(username: String, password: String): Boolean {
        //language=SQLite
        val query = "SELECT 1 FROM users WHERE email = ? AND password = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to username, 2 to password)
        return resultSet.next()
    }

    override fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String,
        newPasswordRepeated: String
    ): Boolean {
        //language=SQLite
        val query = "UPDATE users SET password = ? WHERE email = ?"
        return SQLite.update(query, 1 to newPassword, 2 to email)
    }

}