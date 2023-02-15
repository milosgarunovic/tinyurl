package com.milosgarunovic.tinyurl.plugin

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

val RequestLogging = createApplicationPlugin(name = "CallLogging") {
    val reqStartTimeKey = AttributeKey<Instant>("reqStartTime")
    val reqIdKey = AttributeKey<String>("requestId")

    onCallReceive { call ->
        val request = call.request
        val reqId = UUID.randomUUID().toString()

        call.attributes.put(reqStartTimeKey, Clock.System.now())
        call.attributes.put(reqIdKey, reqId)

        // todo add user@ip
        println("Req id=[ $reqId ]; url=[ ${request.httpMethod.value} ${request.uri} ];")
    }

    onCallRespond { call, _ ->
        val request = call.request

        val reqStartTime = call.attributes[reqStartTimeKey]
        val reqId = call.attributes[reqIdKey]
        val elapsedRequestTime = (Clock.System.now() - reqStartTime).inWholeMilliseconds
        println("Res id=[ $reqId ]; url=[ ${request.httpMethod.value} ${request.uri} ]; status=[${call.response.status()!!.value}]; Finished in ${elapsedRequestTime}ms;")
    }
}