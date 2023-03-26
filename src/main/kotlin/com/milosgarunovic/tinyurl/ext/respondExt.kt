package com.milosgarunovic.tinyurl.ext

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun ApplicationCall.respondStatusCode(status: HttpStatusCode) {
    respond(status, NullBody)
}

suspend inline fun ApplicationCall.respondRedirect(url: String) {
    response.status(HttpStatusCode.MovedPermanently)
    respondRedirect(url, true)
}