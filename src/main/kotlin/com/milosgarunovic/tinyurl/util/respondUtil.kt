package com.milosgarunovic.tinyurl.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun ApplicationCall.respondWithStatusCode(status: HttpStatusCode) {
    response.status(status)
}

suspend inline fun ApplicationCall.respondRedirect(url: String) {
    respondWithStatusCode(HttpStatusCode.MovedPermanently)
    respondRedirect(url, true)
}