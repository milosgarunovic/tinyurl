package com.milosgarunovic.tinyurl.plugin

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import java.time.Instant
import java.util.*

val RequestLogging = createApplicationPlugin(name = "CallLogging") {
    val reqStartTimeKey = AttributeKey<Long>("reqStartTime")
    val reqIdKey = AttributeKey<String>("requestId")

    onCall { call ->
        val request = call.request
        val reqId = UUID.randomUUID().toString()

        call.attributes.put(reqStartTimeKey, Instant.now().toEpochMilli())
        call.attributes.put(reqIdKey, reqId)

        // todo add user@ip
        call.application.environment.log.info("Req id=[ $reqId ]; url=[ ${request.httpMethod.value} ${request.uri} ];")
    }

    onCallRespond { call, _ ->
        val request = call.request

        val reqStartTime = call.attributes[reqStartTimeKey]
        val reqId = call.attributes[reqIdKey]
        val elapsedRequestTime = Instant.now().toEpochMilli() - reqStartTime
        call.application.environment.log.info("Res id=[ $reqId ]; url=[ ${request.httpMethod.value} ${request.uri} ]; status=[${call.response.status()?.value}]; elapsedTime=[${elapsedRequestTime}ms];")
    }
}