package com.milosgarunovic.tinyurl.json.validation

import com.milosgarunovic.tinyurl.exception.BadRequestException

fun validatePassword(password: String, fieldName: String = "password") {
    if (password.length < 8) { // TODO should we put upper limit as well?
        throw BadRequestException("$fieldName must be at least 8 characters long")
    }
    // TODO need to test this for possible combinations
    if (!Regex("^[a-zA-Z0-9!-\\/:-@[-`{-~]]*$").matches(password)) {
        throw BadRequestException("$fieldName must contain lower case letters, upper case letters, numbers and special characters")
    }
}

fun validateEmail(email: String) {
    if (!Regex(".+@.+[\\\\.].+").matches(email)) {
        throw BadRequestException("email not valid")
    }
}