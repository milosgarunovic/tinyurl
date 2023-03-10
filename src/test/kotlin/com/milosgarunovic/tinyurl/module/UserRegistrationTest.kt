package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.SQLite
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*

class UserRegistrationTest {

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

    @Test
    @DisplayName("POST /api/user/register returns 201")
    fun `GET root returns 404`() = testApplication {
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

}