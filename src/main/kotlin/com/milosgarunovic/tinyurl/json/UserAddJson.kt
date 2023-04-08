package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.User
import com.milosgarunovic.tinyurl.json.validation.validateEmail
import com.milosgarunovic.tinyurl.json.validation.validatePassword
import kotlinx.serialization.Serializable

@Serializable
data class UserAddJson(
    val email: String,
    val password: String,
) {

    fun validate() {
        validateEmail(email)
        validatePassword(password)
    }

    fun toUser(encodedPassword: String) = User(email, encodedPassword)
}
