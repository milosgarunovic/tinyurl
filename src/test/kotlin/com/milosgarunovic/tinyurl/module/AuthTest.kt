package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthTest : AbstractTest() {

    private var userAuth = """{"email": "auth.test@test.com", "password": "Password123!"}"""

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()

        testApplication {
            post(client, "/api/user/register", userAuth, token = null)
        }
    }

    @Test
    fun `JWT access token expires after 3 minutes`() = testApplication {
        // ARRANGE
        val client = httpClient()
        InstantUtil.setFixed()

        // ACT
        val token = login(client, userAuth).accessToken

        val shortUrl = post(client, "/api/url", """{"url": "https://test.com"}""", token).bodyAsText()
        val patch = patch(client, "/api/url", """{"id":"$shortUrl", "url":"https://test2.com"}""", token)

        // ASSERT
        assertEquals(HttpStatusCode.OK, patch.status)

        // travel 3 minutes and a couple of seconds into the future and verify that we're not allowed to change the
        // content
        InstantUtil.plusMinutes(3)
        InstantUtil.plusSeconds(3)
        val patchAgain = patch(client, "/api/url", """{"id":"$shortUrl", "url":"https://test2.com"}""", token)
        assertEquals(HttpStatusCode.Unauthorized, patchAgain.status)
        InstantUtil.clear()
    }

    @Test
    fun `JWT refresh token expires after 1 day`() = testApplication {
        // ARRANGE
        val client = httpClient()
        InstantUtil.setFixed()

        // ACT
        val token = login(client, userAuth).refreshToken!!

        val get = client.get("/refreshToken") {
            bearerAuth(token)
        }

        // ASSERT
        assertEquals(HttpStatusCode.OK, get.status)

        InstantUtil.plusDays(1)
        InstantUtil.plusSeconds(3)

        val getAfterADay = client.get("/refreshToken") {
            bearerAuth(token)
        }

        // ASSERT
        assertEquals(HttpStatusCode.Unauthorized, getAfterADay.status)

        InstantUtil.clear()
    }

}