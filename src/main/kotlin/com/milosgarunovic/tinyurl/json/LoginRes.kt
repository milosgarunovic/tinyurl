package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class LoginRes(val accessToken: String, val refreshToken: String)