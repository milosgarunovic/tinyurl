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

// TODO add unit tests for this
fun TinyUrlAddReq.toTinyUrl(shortUrl: String, clock: Clock): TinyUrl {
    val now = clock.instant()
    val calculatedExpiry: Instant? = when (expires) {
        is Expires.At -> {
            if (expires.dateTime.isAfter(now.atZone(ZoneId.of("UTC")))) {
                expires.dateTime.toInstant()
            } else {
                // TODO throw exception because it's not in future, or check this in the request validator
                null
            }
        }

        is Expires.In -> now.plusMillis(expires.milliseconds)
        else -> null
    }

    return TinyUrl(shortUrl = shortUrl, url = this.url, calculatedExpiry = calculatedExpiry)
}