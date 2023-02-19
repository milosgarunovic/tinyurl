package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.TinyUrl
import kotlinx.serialization.Serializable
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@Serializable
data class TinyUrlAddReq(
    val url: String,
    val expires: Expires?,
)

fun TinyUrlAddReq.toTinyUrl(shortUrl: String, clock: Clock): TinyUrl {
    val dateCreated = Instant.now(clock)
    var calculatedExpiry: Instant? = when (expires) {
        is Expires.At -> {
            if (expires.dateTime.isAfter(dateCreated.atZone(ZoneId.of("UTC")))) {
                expires.dateTime.toInstant()
            } else {
                // TODO throw exception because it's not in future, or check this in the request validator
                null
            }
        }

        is Expires.In -> dateCreated.plusMillis(expires.milliseconds)
        else -> null
    }

    return TinyUrl(shortUrl = shortUrl, url = this.url, dateCreated = dateCreated, calculatedExpiry = calculatedExpiry)
}