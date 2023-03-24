package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.ChangePasswordReq
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.user() {

    val userService by inject<UserService>()

    routing {
        route("/api/user") {

            post("/register") {
                val req = call.receive<UserAddJson>()
                val isAdded = userService.add(req)
                if (isAdded) {
                    call.respondStatusCode(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.Conflict, """{"message": "email already exists"}""")
                }
            }

            authenticate("auth-basic") {
                post("/changePassword") {
                    val req = call.receive<ChangePasswordReq>()
                    if (userService.changePassword(call.principal<UserIdPrincipal>()?.name!!, req)) {
                        call.respondStatusCode(HttpStatusCode.OK)
                    } else {
                        call.respondStatusCode(HttpStatusCode.BadRequest) // TODO change to something more meaningful
                    }
                }

                delete("/deleteAccount") {
                    val isAccountDeleted = userService.deleteAccount(call.principal<UserIdPrincipal>()?.name!!)
                    if (isAccountDeleted) {
                        call.respondStatusCode(HttpStatusCode.NoContent)
                    } else {
                        call.respondStatusCode(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
    }
}