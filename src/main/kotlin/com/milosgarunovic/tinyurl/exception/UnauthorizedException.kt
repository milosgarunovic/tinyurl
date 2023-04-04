package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

class UnauthorizedException(override val message: String = "Unauthorized") :
    HttpException(HttpStatusCode.Unauthorized, message)