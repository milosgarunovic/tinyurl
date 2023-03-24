package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User

interface UserRepository {

    fun add(user: User): Boolean

    fun validate(username: String, password: String): Boolean

    fun changePassword(email: String, newPassword: String): Boolean

    fun deleteAccount(email: String): Boolean
}