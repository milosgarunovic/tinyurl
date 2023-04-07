package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthTest : AbstractTest() {

    private var userAuth = """{"email": "auth.test@test.com", "password": "password123"}"""

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()

        testApplication {
            application { mainModule() }
            post(client, "/api/user/register", userAuth, token = null)
        }
    }

    @Test
    fun `JWT token expires after 3 minutes`() = testApplication {
        // ARRANGE
        application { mainModule() }
        val client = httpClient()
        InstantUtil.setFixed()

        // ACT
        val token = login(client, userAuth)

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

}