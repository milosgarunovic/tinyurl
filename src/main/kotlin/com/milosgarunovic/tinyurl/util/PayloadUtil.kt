package com.milosgarunovic.tinyurl.util

import com.auth0.jwt.interfaces.Payload

fun Payload.isNotExpired(): Boolean {
    return getClaim("exp").asLong() > InstantUtil.now().toEpochMilli()
}

// TODO figure out why there is ""test@email.com"" like it's a string with "
//  so we need removeSurrounding
fun Payload.getEmail(): String {
    return getClaim("email").toString().removeSurrounding("\"")
}