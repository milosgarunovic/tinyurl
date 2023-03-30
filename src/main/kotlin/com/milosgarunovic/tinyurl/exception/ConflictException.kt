package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

class ConflictException(message: String) : HttpException(HttpStatusCode.Conflict, message)