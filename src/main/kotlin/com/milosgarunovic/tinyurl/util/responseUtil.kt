package com.milosgarunovic.tinyurl.util

import io.ktor.http.*
import io.ktor.server.application.*

public suspend inline fun ApplicationCall.respondWithStatusCode(status: HttpStatusCode) {
    response.status(status)
}