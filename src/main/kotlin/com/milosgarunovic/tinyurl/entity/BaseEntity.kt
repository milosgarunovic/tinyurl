package com.milosgarunovic.tinyurl.entity

import java.time.Instant
import java.util.*

abstract class BaseEntity(
    var id: String = UUID.randomUUID().toString(),
    var dateCreated: Instant = Instant.now(),
    var active: Boolean = true,
    private var dateDeactivated: Instant? = null,
)