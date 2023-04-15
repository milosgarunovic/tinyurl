package com.milosgarunovic.tinyurl.entity

import java.time.ZonedDateTime

data class Url(
    val shortUrl: String,
    val url: String,
    val expiry: ZonedDateTime? = null,
    val user: User? = null, // column userId
) : BaseEntity()