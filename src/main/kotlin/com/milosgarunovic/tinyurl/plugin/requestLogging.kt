package com.milosgarunovic.tinyurl.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.util.*
import java.time.Instant
import java.util.*

val RequestLogging = createApplicationPlugin(name = "CallLogging") {
    val reqStartTimeKey = AttributeKey<Long>("reqStartTime")
    val reqIdKey = AttributeKey<String>("requestId")
    val usernameKey = AttributeKey<String>("username")
    val ipAddressKey = AttributeKey<String>("ipAddress")

    onCall { call ->
        val request = call.request
        val reqId = UUID.randomUUID().toString()
        val username = call.principal<UserIdPrincipal>()?.name ?: ""
        val ipAddress = call.request.host()

        call.attributes.put(reqStartTimeKey, Instant.now().toEpochMilli())
        call.attributes.put(reqIdKey, reqId)
        call.attributes.put(usernameKey, username)
        call.attributes.put(ipAddressKey, ipAddress)
        // todo add user@ip
        call.application.environment.log.info("Req id=[ $reqId ]; user=[ $username/$ipAddress]; url=[ ${request.httpMethod.value} ${request.uri} ];")
    }

    onCallRespond { call, _ ->
        val request = call.request

        val reqStartTime = call.attributes[reqStartTimeKey]
        val reqId = call.attributes[reqIdKey]
        val username = call.attributes[usernameKey]
        val ipAddress = call.attributes[ipAddressKey]

        val elapsedRequestTime = Instant.now().toEpochMilli() - reqStartTime
        call.application.environment.log.info("Res id=[ $reqId ]; user=[ $username/$ipAddress]; url=[ ${request.httpMethod.value} ${request.uri} ]; status=[${call.response.status()?.value}]; elapsedTime=[${elapsedRequestTime}ms];")
    }
}