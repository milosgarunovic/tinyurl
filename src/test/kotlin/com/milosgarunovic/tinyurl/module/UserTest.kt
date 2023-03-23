package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

class UserTest : AbstractTest() {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            SQLite.setupInMemory()
//            SQLite.setup("test") // used for debugging
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            SQLite.close()
        }
    }

    @Nested
    inner class UserRegisterTest {
        @Test
        @DisplayName("POST /api/user/register returns 201")
        fun `POST api user register returns 201`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"email": "test3@test.com", "password": "password123"}"""
            val response = post(client, "/api/user/register", reqBody)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.Created, response.status)
        }

        @Test
        @DisplayName("POST /api/user/register that already exists returns 409")
        fun `POST api user register that already exists returns 409`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val registerUrl = "/api/user/register"
            val reqBody = """{"email": "test2@test.com", "password": "password123"}"""

            // ACT
            // create a user once
            post(client, registerUrl, reqBody, null)

            // try to add user with the same email again
            val response = post(client, registerUrl, reqBody)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.Conflict, response.status)
            Assertions.assertEquals("""{"message": "email already exists"}""", response.bodyAsText())
        }
    }

    @Nested
    inner class ChangePasswordTest {

        @Test
        @DisplayName("POST /api/user/changePassword")
        fun `POST changePassword`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val email = "test@test.com"
            val oldPassword = "password123"
            val newPassword = "newPassword123"
            val oldBasicAuth = email to oldPassword
            val newBasicAuth = email to newPassword

            // 1. create user
            post(client, "/api/user/register", """{"email": "$email", "password": "$oldPassword"}""")

            // 2. create a resource (that we'll try to change)
            val id = post(client, "/api/tinyUrl", """{"url": "https://test.com"}""", oldBasicAuth).bodyAsText()

            // ACT
            // 3. change password
            val reqBody =
                """{"oldPassword": "$oldPassword", "newPassword": "$newPassword", "newPasswordRepeated": "$newPassword"}"""
            val changePasswordRes = post(client, "/api/user/changePassword", reqBody, oldBasicAuth)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.OK, changePasswordRes.status)

            // 4. user can't access a resource with old password
            val deleteRes = delete(client, "/api/tinyUrl/$id", oldBasicAuth)
            Assertions.assertEquals(HttpStatusCode.Unauthorized, deleteRes.status)

            // 5. user can access a resource with new password
            val deleteResWithNewPassword = delete(client, "/api/tinyUrl/$id", newBasicAuth)
            Assertions.assertEquals(HttpStatusCode.NoContent, deleteResWithNewPassword.status)
        }

        @Test
        @DisplayName("POST /api/user/changePassword 2")
        fun `POST changePassword validation tests`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val email = "test4@test.com"
            val oldPassword = "password123"
            val newPassword = "newPassword123"
            val basicAuth = email to oldPassword

            post(client, "/api/user/register", """{"email": "$email", "password": "$oldPassword"}""")

            // ACT
            val reqBody =
                """{"oldPassword": "wrongPassword", "newPassword": "$newPassword", "newPasswordRepeated": "$newPassword"}"""
            val changePasswordRes = post(client, "/api/user/changePassword", reqBody, basicAuth)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.BadRequest, changePasswordRes.status)

            // ACT
            val reqBody2 =
                """{"oldPassword": "$oldPassword", "newPassword": "$newPassword", "newPasswordRepeated": "notRepeated"}"""
            val changePasswordRes2 = post(client, "/api/user/changePassword", reqBody2, basicAuth)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.BadRequest, changePasswordRes2.status)
        }
    }
}