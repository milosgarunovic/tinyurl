package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.TinyUrl
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TinyUrlAddReq(
    val url: String,
    val expires: Expires?,
)

fun TinyUrlAddReq.toTinyUrl(shortUrl: String) =
    TinyUrl(shortUrl = shortUrl, url = this.url, dateCreated = Instant.now())