package com.milosgarunovic.tinyurl.json

import com.milosgarunovic.tinyurl.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
sealed class Expires {

    @Serializable
    @SerialName("in")
    data class In(val milliseconds: Long) : Expires()

    @Serializable
    @SerialName("at")
    data class At(
        @Serializable(ZonedDateTimeSerializer::class)
        val dateTime: ZonedDateTime
    ) : Expires()
}