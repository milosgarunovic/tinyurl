package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.exception.BadRequestException
import com.milosgarunovic.tinyurl.exception.ConflictException
import com.milosgarunovic.tinyurl.json.ChangePasswordReq
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.json.toUser
import com.milosgarunovic.tinyurl.repository.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserService : KoinComponent {

    private val userRepository by inject<UserRepository>()

    private val passwordService by inject<PasswordService>()

    fun add(user: UserAddJson) {
        val encodedPassword = passwordService.encode(user.password)
        val isUserAdded = userRepository.add(user.toUser(encodedPassword))
        if (!isUserAdded) {
            throw ConflictException("""{"message": "email already exists"}""")
        }
    }

    fun validate(email: String, password: String): Boolean {
        val encodedPassword = userRepository.getPassword(email)
        if (encodedPassword != null) {
            return passwordService.validate(password, encodedPassword)
        }
        return false
    }

    fun changePassword(email: String, changePasswordReq: ChangePasswordReq): Boolean {
        if (validate(email, changePasswordReq.oldPassword) &&
            changePasswordReq.newPassword == changePasswordReq.newPasswordRepeated
        ) {
            val encodedPassword = passwordService.encode(changePasswordReq.newPassword)
            return userRepository.changePassword(email, encodedPassword)
        }
        return false
    }

    fun deleteAccount(email: String, confirmPassword: String): Boolean {
        if (validate(email, confirmPassword)) {
            return userRepository.deleteAccount(email)
        }
        throw BadRequestException("confirmPassword field is not correct.")
    }

    // TODO fun forgotPassword() - this will have dependency on some kind of email service

    // TODO fun changeEmail() - user might want to change email so we should implement this as well

}