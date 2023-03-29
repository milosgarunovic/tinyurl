package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.Url
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class UrlAddReq(
    val url: String,
    val expires: Expires?,
) {

    // TODO add unit tests for this
    fun toTinyUrl(shortUrl: String): Url {
        val now = Instant.now()
        val calculatedExpiry: Instant? = when (expires) {
            // TODO maybe save this in a date format and check on get if it's valid. This would be for saving future dates
            //   as text/timestamp and would be more correct if anything changes in the future.
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

        return Url(shortUrl, this.url, calculatedExpiry)
    }
}

