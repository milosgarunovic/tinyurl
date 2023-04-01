package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.*
import io.ktor.client.call.*
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
            post(client, registerUrl, reqBody)

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
            val client = httpClient()
            val email = "test@test.com"
            val oldPassword = "password123"
            val newPassword = "newPassword123"

            // 1. create user
            val registerAndLogin = """{"email": "$email", "password": "$oldPassword"}"""
            post(client, "/api/user/register", registerAndLogin)
            // login
            val token = login(client, registerAndLogin)

            // 2. create a resource (that we'll try to change)
            val id = post(client, "/api/url", """{"url": "https://test.com"}""", token).bodyAsText()

            // ACT
            // 3. change password
            val reqBody =
                """{"oldPassword": "$oldPassword", "newPassword": "$newPassword", "newPasswordRepeated": "$newPassword"}"""
            val changePasswordRes = post(client, "/api/user/changePassword", reqBody, token)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.OK, changePasswordRes.status)

            // TODO user can't log in with old password

            val newToken = login(client, """{"email": "$email", "password": "$newPassword"}""")
            // TODO same as in POST /api/user/deleteAccount, I need to solve JWT issue to make this work.
            // 4. user can't access a resource with old password
//            val deleteRes = delete(client, "/api/url/$id", newToken)
//            Assertions.assertEquals(HttpStatusCode.Unauthorized, deleteRes.status)

            // 5. user can access a resource with new password
            val deleteResWithNewPassword = delete(client, "/api/url/$id", newToken)
            Assertions.assertEquals(HttpStatusCode.NoContent, deleteResWithNewPassword.status)
        }

        @Test
        @DisplayName("POST /api/user/changePassword validation tests")
        fun `POST changePassword validation tests`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val email = "test4@test.com"
            val oldPassword = "password123"
            val newPassword = "newPassword123"

            val reqBody1 = """{"email": "$email", "password": "$oldPassword"}"""
            post(client, "/api/user/register", reqBody1, token = null)
            val token = login(client, reqBody1)

            // ACT
            val reqBody =
                """{"oldPassword": "wrongPassword", "newPassword": "$newPassword", "newPasswordRepeated": "$newPassword"}"""
            val changePasswordRes = post(client, "/api/user/changePassword", reqBody, token)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.BadRequest, changePasswordRes.status)

            // ACT
            val reqBody2 =
                """{"oldPassword": "$oldPassword", "newPassword": "$newPassword", "newPasswordRepeated": "notRepeated"}"""
            val changePasswordRes2 = post(client, "/api/user/changePassword", reqBody2, token)

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.BadRequest, changePasswordRes2.status)
        }
    }

    @Nested
    inner class DeleteAccountTest {
        @Test
        @DisplayName("POST /api/user/deleteAccount")
        fun `POST deleteAccount`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val email = "accountToBeDeleted@test.com"
            val password = "Password123!"

            // create a user
            val reqBody = """{"email": "$email", "password": "$password"}"""
            post(client, "/api/user/register", reqBody, token = null)
            val token = login(client, reqBody)

            // create an url that we'll test
            val id = post(client, "/api/url", """{"url": "https://test.com"}""", token).bodyAsText()

            // ACT
            val deleteReqBody = """{"confirmPassword": "$password"}"""
            val deleteRes = post(client, "/api/user/deleteAccount", deleteReqBody, token)

            // Assert
            Assertions.assertEquals(HttpStatusCode.NoContent, deleteRes.status)

            // urls are still accessible
            val getResponse = httpClient().get("/$id")
            Assertions.assertEquals(HttpStatusCode.MovedPermanently, getResponse.status)

            // TODO need to make this work, this is JWT "issue", because we can't confirm without some sort of cache
            //  if this token is still valid.
            // change of url isn't accessible, this account no longer exists so there's no modifying it
//            val deleteUrlRes = delete(client, "/api/url/$id", token)
//            Assertions.assertEquals(HttpStatusCode.Unauthorized, deleteUrlRes.status)
        }

        @Test
        @DisplayName("POST deleteAccount with wrong confirmPassword")
        fun `POST deleteAccount with wrong confirmPassword`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val email = "accountToBeDeleted2@test.com"
            val password = "Password123!"
            val client = httpClient()

            // create a user
            val reqBody = """{"email": "$email", "password": "$password"}"""
            post(client, "/api/user/register", reqBody, token = null)
            val token = login(client, reqBody)

            // ACT
            val deleteReqBody = """{"confirmPassword": "wrongPassword"}"""
            val deleteRes = post(client, "/api/user/deleteAccount", deleteReqBody, token)

            // Assert
            Assertions.assertEquals(HttpStatusCode.BadRequest, deleteRes.status)
            Assertions.assertEquals("confirmPassword field is not correct.", deleteRes.bodyAsText())
        }
    }
}