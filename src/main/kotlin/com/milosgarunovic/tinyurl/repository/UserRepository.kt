package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.User

interface UserRepository {

    fun add(user: User)

    fun validate(username: String, password: String): Boolean

//    fun updatePassword()

//    fun deleteAccount()
}