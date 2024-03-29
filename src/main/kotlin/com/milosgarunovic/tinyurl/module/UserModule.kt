package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.exception.NotFoundException
import com.milosgarunovic.tinyurl.ext.respondStatusCode
import com.milosgarunovic.tinyurl.json.ChangePasswordReq
import com.milosgarunovic.tinyurl.json.DeleteAccountReq
import com.milosgarunovic.tinyurl.json.UserAddJson
import com.milosgarunovic.tinyurl.module.AuthType.JWT
import com.milosgarunovic.tinyurl.service.PropertiesService
import com.milosgarunovic.tinyurl.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.userModule() {

    val userService by inject<UserService>()

    val propertiesService by inject<PropertiesService>()

    routing {
        route("/api/user") {

            post("/register") {
                if (!propertiesService.isRegistrationEnabled()) {
                    throw NotFoundException()
                }
                val req = call.receive<UserAddJson>()
                req.validate()
                userService.add(req)
                call.respondStatusCode(HttpStatusCode.Created)
            }

            authenticate(JWT.type) {
                post("/changePassword") {
                    val req = call.receive<ChangePasswordReq>()
                    req.validate()

                    userService.changePassword(call.principal<JWTPrincipal>()?.get("email")!!, req)
                    call.respondStatusCode(HttpStatusCode.OK)
                }

                // Delete account is POST because it needs to have a body. DELETE method doesn't have body.
                post("/deleteAccount") {
                    val req = call.receive<DeleteAccountReq>()
                    val email = call.principal<JWTPrincipal>()?.get("email")!!
                    userService.deleteAccount(email, req.confirmPassword)
                    call.respondStatusCode(HttpStatusCode.NoContent)
                }
            }
        }
    }
}