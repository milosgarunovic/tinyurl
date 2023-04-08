package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.json.validation.validatePassword
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordReq(val oldPassword: String, val newPassword: String, val newPasswordRepeated: String) {

    fun validate() {
        validatePassword(newPassword, "newPassword")
        validatePassword(newPasswordRepeated, "newPasswordRepeated")
    }
}