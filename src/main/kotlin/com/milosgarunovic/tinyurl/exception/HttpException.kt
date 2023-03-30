package com.milosgarunovic.tinyurl.exception

import io.ktor.http.*

abstract class HttpException(val statusCode: HttpStatusCode, override val message: String) : Exception(message)