package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordReq(val oldPassword: String, val newPassword: String, val newPasswordRepeated: String)