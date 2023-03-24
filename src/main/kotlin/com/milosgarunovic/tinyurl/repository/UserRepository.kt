package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User

interface UserRepository {

    fun add(user: User): Boolean

    fun getPassword(email: String): String?

    fun changePassword(email: String, newPassword: String): Boolean

    fun deleteAccount(email: String): Boolean
}