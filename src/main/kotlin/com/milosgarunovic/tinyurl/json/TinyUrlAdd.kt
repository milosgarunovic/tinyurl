package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class TinyUrlAdd(val actualUrl: String)