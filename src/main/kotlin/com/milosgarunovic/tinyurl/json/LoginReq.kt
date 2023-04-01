package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class LoginReq(val email: String, val password: String)