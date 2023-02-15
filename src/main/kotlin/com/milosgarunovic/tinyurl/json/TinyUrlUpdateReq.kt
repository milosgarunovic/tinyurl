package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class TinyUrlUpdateReq(val id: String, val url: String)