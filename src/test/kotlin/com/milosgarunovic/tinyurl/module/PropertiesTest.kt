package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.PropertiesJson
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        val body: PropertiesJson = getPropertiesResponse.body<PropertiesJson>()

        assertEquals(HttpStatusCode.OK, getPropertiesResponse.status)
        assertTrue(body.publicUrlCreation)
        assertTrue(body.registrationEnabled)
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

    @Test
    fun `update publicUrlCreation and registrationEnabled`() = testApplication {
        val client = httpClient()
        val token = login(client, adminAuth).accessToken

        val disableRegistration = post(client, "/properties/disableRegistration", token = token)
        assertEquals(HttpStatusCode.OK, disableRegistration.status)
        assertTrue(disableRegistration.body<PropertiesJson>().publicUrlCreation)
        assertFalse(disableRegistration.body<PropertiesJson>().registrationEnabled)

        val disablePublicUrlCreation = post(client, "/properties/disablePublicUrlCreation", token = token)
        assertEquals(HttpStatusCode.OK, disablePublicUrlCreation.status)
        assertFalse(disablePublicUrlCreation.body<PropertiesJson>().publicUrlCreation)
        assertFalse(disablePublicUrlCreation.body<PropertiesJson>().registrationEnabled)

        val enableRegistration = post(client, "/properties/enableRegistration", token = token)
        assertEquals(HttpStatusCode.OK, enableRegistration.status)
        assertFalse(enableRegistration.body<PropertiesJson>().publicUrlCreation)
        assertTrue(enableRegistration.body<PropertiesJson>().registrationEnabled)

        val enablePublicUrlCreation = post(client, "/properties/enablePublicUrlCreation", token = token)
        assertEquals(HttpStatusCode.OK, enablePublicUrlCreation.status)
        assertTrue(enablePublicUrlCreation.body<PropertiesJson>().publicUrlCreation)
        assertTrue(enablePublicUrlCreation.body<PropertiesJson>().registrationEnabled)
    }
}