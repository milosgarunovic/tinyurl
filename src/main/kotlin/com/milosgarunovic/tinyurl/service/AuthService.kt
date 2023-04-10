package com.milosgarunovic.tinyurl.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.milosgarunovic.tinyurl.exception.UnauthorizedException
import com.milosgarunovic.tinyurl.json.LoginReq
import com.milosgarunovic.tinyurl.json.LoginRes
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.server.config.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.time.temporal.ChronoUnit

class AuthService : KoinComponent {

    private val userService by inject<UserService>()

    private val applicationConfig by inject<ApplicationConfig>()

    fun login(loginReq: LoginReq): LoginRes {
        val email = loginReq.email
        if (!userService.isUserValid(email, loginReq.password)) {
            throw UnauthorizedException()
        }

        val accessSecret = applicationConfig.property("jwt.accessTokenSecret").getString()
        val atExpiry = applicationConfig.property("jwt.accessTokenExpiry").getString().toLong()
        val atExpiryUnit = applicationConfig.property("jwt.accessTokenExpiryUnit").getString()

        val refreshSecret = applicationConfig.property("jwt.refreshTokenSecret").getString()
        val rtExpiry = applicationConfig.property("jwt.refreshTokenExpiry").getString().toLong()
        val rtExpiryUnit = applicationConfig.property("jwt.refreshTokenExpiryUnit").getString()

        val accessToken = jwt(email, accessSecret, Duration.of(atExpiry, ChronoUnit.valueOf(atExpiryUnit)))
        val refreshToken = jwt(email, refreshSecret, Duration.of(rtExpiry, ChronoUnit.valueOf(rtExpiryUnit)))
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
    fun refreshToken(refreshToken: String): LoginRes {
        val jwt = JWT.decode(refreshToken)
        val expiration = jwt.getClaim("exp").asLong()
        // if expiration is in the past
        if (expiration < InstantUtil.now().toEpochMilli()) {
            throw UnauthorizedException("Refresh token expired, please login again.")
        }

        val accessSecret = applicationConfig.property("jwt.accessTokenSecret").getString()
        val atExpiry = applicationConfig.property("jwt.accessTokenExpiry").getString().toLong()
        val atExpiryUnit = applicationConfig.property("jwt.accessTokenExpiryUnit").getString()

        val email = jwt.getClaim("email").asString()
        val accessToken = jwt(email, accessSecret, Duration.of(atExpiry, ChronoUnit.valueOf(atExpiryUnit)))
        return LoginRes(accessToken)
    }

    private fun jwt(email: String, secret: String, expiry: Duration): String {
        return JWT.create()
//                .withAudience(audience)
//                .withIssuer(issuer)
            .withClaim("email", email)
            .withClaim("exp", InstantUtil.now().plusMillis(expiry.toMillis()).toEpochMilli())
            .sign(Algorithm.HMAC256(secret))
    }
}