package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.json.toUser

class UserInMemoryRepository {

    private val users: MutableList<User> = ArrayList()

    fun add(req: UserAddJson) {
        // TODO checks
        //   unique
        //   password minimum 8 chars
        users.add(req.toUser())
    }

    fun getByUsername(username: String): User? {
        return users.firstOrNull { it.username == username }
    }

    fun validate(username: String, password: String): Boolean {
        // password still isn't hashed
        return users.firstOrNull { it.username == username && it.password == password } != null
    }

    fun delete() {

    }

    fun updatePassword() {

    }

}