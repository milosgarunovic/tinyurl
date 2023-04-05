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

    fun login(loginReq: LoginReq, accessTokenSecret: String, refreshTokenSecret: String): LoginRes {
        if (!userService.isUserValid(loginReq.email, loginReq.password)) {
            throw UnauthorizedException()
        }

        val accessToken = jwt(loginReq.email, accessTokenSecret, Duration.ofMinutes(3))
        val refreshToken = jwt(loginReq.email, refreshTokenSecret, Duration.ofHours(24))
        return LoginRes(accessToken, refreshToken)
    }

    /**
     * Refresh token works kinda the same as logging in. The difference is that when logging in, you're sending
     * sensitive information over the network - username and password. So we should minimize amount of logging in as
     * much as possible. For that, we have tokens, and refresh token is a substitute for username and password and works
     * for some period of time. Refresh token is sent in Authorization header, but it will work only in /refreshToken
     * path. It works that way so users can't use refreshToken instead of access token. This is done by using two
     * different secrets to distinguish access token and refresh token.
     */
    fun refreshToken(refreshToken: String, accessTokenSecret: String): LoginRes {
        val jwt = JWT.decode(refreshToken)
        val expiration = jwt.getClaim("exp").asLong()
        if (Instant.now().toEpochMilli() < expiration) {
            throw UnauthorizedException("Refresh token expired, please login again.")
        }

        val email = jwt.getClaim("email").asString()
        val accessToken = jwt(email, accessTokenSecret, Duration.ofMinutes(3))
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