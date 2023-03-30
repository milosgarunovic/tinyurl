package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.ChangePasswordReq
import com.milosgarunovic.tinyurl.json.DeleteAccountReq
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.userModule() {

    val userService by inject<UserService>()

    routing {
        route("/api/user") {

            post("/register") {
                val req = call.receive<UserAddJson>()
                userService.add(req)
                call.respondStatusCode(HttpStatusCode.Created)
            }

            authenticate("auth-basic") {
                post("/changePassword") {
                    val req = call.receive<ChangePasswordReq>()
                    if (userService.changePassword(call.principal<UserIdPrincipal>()?.name!!, req)) {
                        call.respondStatusCode(HttpStatusCode.OK)
                    } else {
                        // TODO instead of this, throw an exception
                        call.respondStatusCode(HttpStatusCode.BadRequest) // TODO change to something more meaningful
                    }
                }

                post("/deleteAccount") {
                    val req = call.receive<DeleteAccountReq>()
                    val email = call.principal<UserIdPrincipal>()?.name!!
                    val isAccountDeleted = userService.deleteAccount(email, req.confirmPassword)
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