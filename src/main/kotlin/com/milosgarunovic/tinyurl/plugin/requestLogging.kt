package com.milosgarunovic.tinyurl.plugin

import com.auth0.jwt.JWT
import com.milosgarunovic.tinyurl.util.getEmail
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.*
import java.time.Instant
import java.util.*

val RequestLogging = createApplicationPlugin(name = "RequestLogging") {
    val reqStartTimeKey = AttributeKey<Long>("reqStartTime")
    val reqIdKey = AttributeKey<String>("requestId")
    val usernameKey = AttributeKey<String>("username")
    val ipAddressKey = AttributeKey<String>("ipAddress")

    on(CallSetup) { call ->
        val request = call.request
        val reqId = UUID.randomUUID().toString()
        val email = getEmail(request)
        val ipAddress = call.request.origin.remoteAddress

        call.attributes.put(reqStartTimeKey, Instant.now().toEpochMilli())
        call.attributes.put(reqIdKey, reqId)
        call.attributes.put(usernameKey, email)
        call.attributes.put(ipAddressKey, ipAddress)
        val httpMethod = request.httpMethod.value
        val url = request.uri

        call.application.environment.log.info("Req " + commonMessage(reqId, email, ipAddress, httpMethod, url))
    }

    on(ResponseSent) { call ->
        val request = call.request

        val reqStartTime = call.attributes[reqStartTimeKey]
        val reqId = call.attributes[reqIdKey]
        val username = call.attributes[usernameKey]
        val ipAddress = call.attributes[ipAddressKey]
        val httpMethod = request.httpMethod.value
        val url = request.uri
        val elapsedRequestTime = Instant.now().toEpochMilli() - reqStartTime
        val httpStatusCode = call.response.status()!!.value

        val common = commonMessage(reqId, username, ipAddress, httpMethod, url)
        val onRespond = "status=[ $httpStatusCode ]; elapsedTime=[ ${elapsedRequestTime}ms ];"
        call.application.environment.log.info("Res $common $onRespond")
    }
}

private fun getEmail(request: ApplicationRequest): String {
//  val username = call.principal<UserIdPrincipal>("auth-basic")?.name ?: "" // doesn't work
    // workaround is to parse basic auth myself
    if (request.authorization()?.removePrefix("Bearer ") != null) {
        return JWT.decode(request.authorization()?.removePrefix("Bearer ")).getEmail() + "@"
    }
    return ""
}

fun commonMessage(reqId: String, email: String, ipAddress: String, httpMethod: String, url: String): String {
    return "id=[ $reqId ]; user=[ $email$ipAddress ]; url=[ $httpMethod $url ];"
}