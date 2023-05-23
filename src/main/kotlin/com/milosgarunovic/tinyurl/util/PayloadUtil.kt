package com.milosgarunovic.tinyurl.util

import com.auth0.jwt.interfaces.Payload

fun Payload.isNotExpired(): Boolean {
    return getClaim("exp").asLong() > InstantUtil.now().toEpochMilli()
}

fun Payload.getEmail(): String {
    return getClaim("email").toString().removeSurrounding("\"")
}