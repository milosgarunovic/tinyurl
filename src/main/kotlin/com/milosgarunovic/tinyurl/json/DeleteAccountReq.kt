package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountReq(val confirmPassword: String)