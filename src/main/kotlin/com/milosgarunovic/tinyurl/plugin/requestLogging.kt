package com.milosgarunovic.tinyurl.plugin

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import java.time.Instant
import java.util.*

val RequestLogging = createApplicationPlugin(name = "RequestLogging") {
    val reqStartTimeKey = AttributeKey<Long>("reqStartTime")
    val reqIdKey = AttributeKey<String>("requestId")
    val usernameKey = AttributeKey<String>("username")
    val ipAddressKey = AttributeKey<String>("ipAddress")

    onCall { call ->
        val request = call.request
        val reqId = UUID.randomUUID().toString()
        val username = getUsername(request)
        val ipAddress = call.request.host()

        call.attributes.put(reqStartTimeKey, Instant.now().toEpochMilli())
        call.attributes.put(reqIdKey, reqId)
        call.attributes.put(usernameKey, username)
        call.attributes.put(ipAddressKey, ipAddress)
        val httpMethod = request.httpMethod.value
        val url = request.uri

        call.application.environment.log.info(commonMessage(reqId, username, ipAddress, httpMethod, url))
    }

    onCallRespond { call, _ ->
        val request = call.request

        val reqStartTime = call.attributes[reqStartTimeKey]
        val reqId = call.attributes[reqIdKey]
        val username = call.attributes[usernameKey]
        val ipAddress = call.attributes[ipAddressKey]
        val httpMethod = request.httpMethod.value
        val url = request.uri
        val elapsedRequestTime = Instant.now().toEpochMilli() - reqStartTime
        val httpStatusCode = call.response.status()?.value

        val common = commonMessage(reqId, username, ipAddress, httpMethod, url)
        val onRespond = "status=[$httpStatusCode]; elapsedTime=[${elapsedRequestTime}ms];"
        call.application.environment.log.info("$common $onRespond")
    }
}

private fun getUsername(request: ApplicationRequest): String {
//  val username = call.principal<UserIdPrincipal>("auth-basic")?.name ?: "" // doesn't work
    // workaround is to parse basic auth myself
    val username = request.authorization()?.removePrefix("Basic ")?.decodeBase64String()?.split(":")?.get(0)
    return if (username != null) "$username@" else ""
}

fun commonMessage(reqId: String, username: String, ipAddress: String, httpMethod: String, url: String): String {
    return "Res id=[ $reqId ]; user=[ $username$ipAddress ]; url=[ $httpMethod $url ];"
}