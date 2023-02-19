package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class TinyUrlAddReq(
    val url: String,
    val expires: Expires?,
)