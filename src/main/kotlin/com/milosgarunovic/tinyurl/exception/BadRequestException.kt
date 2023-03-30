package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

class BadRequestException(message: String) : HttpException(HttpStatusCode.BadRequest, message)