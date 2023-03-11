package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.json.toUser
import com.milosgarunovic.tinyurl.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.user() {

    val userRepository by inject<UserRepository>()

    routing {
        route("/api/user/register") {

            post {
                val req = call.receive<UserAddJson>()
                val isAdded = userRepository.add(req.toUser())
                if (isAdded) {
                    call.respondStatusCode(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.Conflict, """{"message": "email already exists"}""")
                }
            }
        }
    }
}