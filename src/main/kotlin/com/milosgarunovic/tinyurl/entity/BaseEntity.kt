package com.milosgarunovic.tinyurl.entity

import java.time.Instant
import java.util.*

abstract class BaseEntity(
    val id: String = UUID.randomUUID().toString(),
    val dateCreated: Instant = Instant.now(),
    var active: Boolean = true,
    var dateDeactivated: Instant? = null,
) {
    fun deactivate() {
        active = false
        dateDeactivated = Instant.now()
    }
}