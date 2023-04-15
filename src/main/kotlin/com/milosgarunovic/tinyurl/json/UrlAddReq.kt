package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.entity.Url
import com.milosgarunovic.tinyurl.exception.BadRequestException
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class UrlAddReq(
    val url: String,
    val expires: Expires?,
) {

    fun toTinyUrl(shortUrl: String): Url {
        val now = Instant.now()
        val zonedDateTime = when (expires) {
            is Expires.At -> {
                if (expires.dateTime.isAfter(now.atZone(ZoneId.of("UTC")))) {
                    expires.dateTime
                } else {
                    throw BadRequestException("expired.dateTime can't be in past")
                }
            }

            is Expires.In -> {
                if (expires.milliseconds > 0) {
                    now.plusMillis(expires.milliseconds).atZone(ZoneId.of("UTC"))
                } else {
                    throw BadRequestException("expired.milliseconds can't be of negative value")
                }
            }

            else -> null
        }

        return Url(shortUrl, this.url, zonedDateTime)
    }
}

