package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

class UserTest {

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
            val response = client.post("/api/user/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email": "test@test.com", "password": "password123"}""")
            }

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.Created, response.status)
        }

        @Test
        @DisplayName("POST /api/user/register that already exists returns 409")
        fun `POST api user register that already exists returns 409`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            client.post("/api/user/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email": "test2@test.com", "password": "password123"}""")
            }

            // try to add the same email again
            val response = client.post("/api/user/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email": "test2@test.com", "password": "password123"}""")
            }

            // ASSERT
            Assertions.assertEquals(HttpStatusCode.Conflict, response.status)
            Assertions.assertEquals("""{"message": "email already exists"}""", response.bodyAsText())
        }
    }
}