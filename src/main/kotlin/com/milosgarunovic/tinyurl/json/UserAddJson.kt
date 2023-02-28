package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.User
import kotlinx.serialization.Serializable

@Serializable
class UserAddJson(
    val username: String,
    val email: String,
    val password: String,
)

fun UserAddJson.toUser() = User(username, email, password)