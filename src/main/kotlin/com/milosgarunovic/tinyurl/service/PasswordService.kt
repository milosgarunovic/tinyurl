package com.milosgarunovic.tinyurl.service

import com.password4j.Password

class PasswordService {

    fun encode(plainPassword: String): String {
        return Password.hash(plainPassword).withBcrypt().result!!
    }

    fun validate(plainPassword: String, encodedPassword: String): Boolean {
        return Password.check(plainPassword, encodedPassword).withBcrypt()
    }

}