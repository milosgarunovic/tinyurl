package com.milosgarunovic.tinyurl.entity

import java.time.Instant

data class TinyUrl(
    val shortUrl: String,
    val url: String,
    val calculatedExpiry: Instant? = null,
    val user: User? = null, // column userId
) : BaseEntity()