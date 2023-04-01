package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.exception.UnauthorizedException
import com.milosgarunovic.tinyurl.json.LoginReq
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginService : KoinComponent {

    private val userService by inject<UserService>()

    fun validate(loginReq: LoginReq) {
        if (!userService.isUserValid(loginReq.email, loginReq.password)) {
            throw UnauthorizedException()
        }
    }
}