package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

class UnauthorizedException : HttpException(HttpStatusCode.Unauthorized, "Unauthorized")