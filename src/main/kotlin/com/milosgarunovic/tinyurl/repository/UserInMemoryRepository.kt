package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User

class UserInMemoryRepository {

    private val users: MutableList<User> = ArrayList()

    fun add() {

    }

    fun getByUsername(username: String): User? {
        return users.firstOrNull { it.username == username }
    }

    fun delete() {

    }

    fun updatePassword() {

    }

}