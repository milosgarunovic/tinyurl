package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.User
import com.milosgarunovic.tinyurl.exception.BadRequestException
import kotlinx.serialization.Serializable

@Serializable
data class UserAddJson(
    val email: String,
    val password: String,
) {

    fun validate() {
        if (!Regex(".+@.+[\\\\.].+").matches(email)) {
            throw BadRequestException("email not valid")
        }
        if (password.length < 8) { // TODO should we put upper limit as well?
            throw BadRequestException("password must be at least 8 characters long")
        }
        // TODO need to test this for possible combinations
        if (!Regex("^[a-zA-Z0-9!-\\/:-@[-`{-~]]*$").matches(password)) {
            throw BadRequestException("password must contain lower case letters, upper case letters, numbers and special characters")
        }
    }

    fun toUser(encodedPassword: String) = User(email, encodedPassword)
}
