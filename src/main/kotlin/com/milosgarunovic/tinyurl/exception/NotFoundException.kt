package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

class NotFoundException(message: String = "Resource not found") : HttpException(HttpStatusCode.NotFound, message)
