package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.repository.UserInMemoryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Application.user(userRepository: UserInMemoryRepository) {

    routing {
        route("/api/user/register") {

            post {
                val req = call.receive<UserAddJson>()
                userRepository.add(req)
                call.respondStatusCode(HttpStatusCode.Created)
            }

        }
    }
}