package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PropertiesTest : AbstractTest() {

    private val adminEmail = "admin@test.com"
    private val adminAuth = """{"email": "$adminEmail", "password": "Password123!"}"""
    private val userAuth = """{"email": "auth.test@test.com", "password": "Password123!"}"""

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()

        testApplication {
            post(client, "/api/user/register", adminAuth, token = null)
            post(client, "/api/user/register", userAuth, token = null)

            // sets the created user to be admin in db, this is the only way to set admin user
            SQLite.update("UPDATE users SET is_admin = true WHERE email = ?;", 1 to adminEmail)
        }
    }

    @Test
    fun `getProperties with admin user`() = testApplication {
        val client = httpClient()
        val token = login(client, adminAuth).accessToken

        val getPropertiesResponse = client.get("/properties") {
            bearerAuth(token)
        }

        assertEquals(HttpStatusCode.OK, getPropertiesResponse.status)
    }

    @Test
    fun `getProperties with non admin user`() = testApplication {
        val client = httpClient()
        val token = login(client, userAuth).accessToken

        val getPropertiesResponse = client.get("/properties") {
            bearerAuth(token)
        }

        assertEquals(HttpStatusCode.NotFound, getPropertiesResponse.status)
    }
}