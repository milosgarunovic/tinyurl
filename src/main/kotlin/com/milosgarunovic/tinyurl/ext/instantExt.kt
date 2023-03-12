package com.milosgarunovic.tinyurl.ext

import java.time.Instant

fun Instant?.milli(): Long = this?.toEpochMilli() ?: 0

