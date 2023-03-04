package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.User
import kotlinx.serialization.Serializable

@Serializable
class UserAddJson(
    val email: String,
    val password: String,
)

fun UserAddJson.toUser() = User(email, password)