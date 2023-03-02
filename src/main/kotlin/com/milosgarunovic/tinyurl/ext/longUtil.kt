package com.milosgarunovic.tinyurl.ext

import java.time.Instant

fun Long.toInstant() = Instant.ofEpochMilli(this)

fun Long.toBoolean() = this == 1L