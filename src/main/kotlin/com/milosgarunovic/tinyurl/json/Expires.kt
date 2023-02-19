package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class Expires {

    @Serializable
    @SerialName("in")
    data class In(val milliseconds: Long) : Expires()

    @Serializable
    @SerialName("at")
    data class At(@Contextual val dateTime: LocalDateTime) : Expires()
}