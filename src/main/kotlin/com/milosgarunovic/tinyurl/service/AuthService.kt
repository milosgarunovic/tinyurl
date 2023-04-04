package com.milosgarunovic.tinyurl.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.milosgarunovic.tinyurl.exception.UnauthorizedException
import com.milosgarunovic.tinyurl.json.LoginReq
import com.milosgarunovic.tinyurl.json.LoginRes
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.time.Instant

class AuthService : KoinComponent {

    private val userService by inject<UserService>()

    // secret and expiry are temporary, should be stored in config file
    private val secret = "483f1296-30d0-41df-8376-120fc793d9eb"

    fun login(loginReq: LoginReq): LoginRes {
        if (!userService.isUserValid(loginReq.email, loginReq.password)) {
            throw UnauthorizedException()
        }


        val accessToken = jwt(loginReq.email, secret, Duration.ofMinutes(3))
        val refreshToken = jwt(loginReq.email, secret, Duration.ofHours(24))
        return LoginRes(accessToken, refreshToken)
    }

    fun refreshToken(refreshToken: String): LoginRes {
        val jwt = JWT.decode(refreshToken)
        val expiration = jwt.getClaim("exp").asLong()
        if (expiration < Instant.now().toEpochMilli()) {
            throw UnauthorizedException("Refresh token expired, please login again.")
        }

        val email = jwt.getClaim("email").asString()
        val accessToken = jwt(email, secret, Duration.ofMinutes(3))
        return LoginRes(accessToken)
    }

    private fun jwt(email: String, secret: String, expiry: Duration): String {
        return JWT.create()
//                .withAudience(audience)
//                .withIssuer(issuer)
            .withClaim("email", email)
            .withClaim("exp", Instant.now().plusMillis(expiry.toMillis()).toEpochMilli())
            .sign(Algorithm.HMAC256(secret))
    }
}