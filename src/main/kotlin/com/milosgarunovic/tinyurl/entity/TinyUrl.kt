package com.milosgarunovic.tinyurl.entity

import com.milosgarunovic.tinyurl.json.Expires
import java.time.Instant

data class TinyUrl(
    val shortUrl: String,
    val url: String,
    /**
     * calculated if [expires] field is present, and it's adjusted based on [dateCreated] when [Expires.In] is used.
     * In case of [Expires.At] it's that date. Must be ahead of [dateCreated].
     */
    val calculatedExpiry: Instant? = null,
) : BaseEntity()