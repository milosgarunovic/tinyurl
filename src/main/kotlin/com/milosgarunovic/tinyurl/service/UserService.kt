package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.json.ChangePasswordReq
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.json.toUser
import com.milosgarunovic.tinyurl.repository.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserService : KoinComponent {

    private val userRepository by inject<UserRepository>()

    fun add(user: UserAddJson): Boolean {
        return userRepository.add(user.toUser())
    }

    fun validate(username: String, password: String): Boolean {
        return userRepository.validate(username, password)
    }

    fun changePassword(email: String, changePasswordReq: ChangePasswordReq): Boolean {
        if (validate(email, changePasswordReq.oldPassword) &&
            changePasswordReq.newPassword == changePasswordReq.newPasswordRepeated
        ) {
            return userRepository.changePassword(email, changePasswordReq.newPassword)
        }
        return false
    }

    // TODO fun forgotPassword() - this will have dependency on some kind of email service

}