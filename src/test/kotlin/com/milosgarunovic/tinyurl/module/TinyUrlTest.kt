package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TinyUrlTest {

    @Nested
    inner class GetRootTests {
        @Test
        fun `GET root - returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.get("/")

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        fun `GET root with path - returns 301`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // custom client that doesn't follow redirects
            val client = createClient {
                followRedirects = false
            }
            val expectedUrl = """https://test.com"""

            // ACT

            // create a new post
            val createdResponse = client.post("/api/tinyUrl") {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "$expectedUrl"}""")
            }
            val id = createdResponse.bodyAsText()

            // get created url
            val response = client.get("/$id")

            // ASSERT
            assertEquals(HttpStatusCode.MovedPermanently, response.status)
            assertEquals(expectedUrl, response.headers["Location"])
        }
    }

    @Nested
    inner class PostApiTinyUrlTests {

        @Test
        fun `POST api-tinyUrl with body returns 201 and has length 8`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.post("/api/tinyUrl") {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "https://test.com"}""")
            }

            // ASSERT
            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals(8, response.bodyAsText().length)
        }

    }

    @Nested
    inner class PatchApiTinyUrlTests {

        @Test
        fun `PATCH api-tinyUrl with body returns 200`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val createdResponse = client.post("/api/tinyUrl") {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "https://test.com"}""")
            }

            val id = createdResponse.bodyAsText()
            val response = client.patch("/api/tinyUrl") {
                contentType(ContentType.Application.Json)
                setBody("""{"id":"$id", "url":"https://test2.com"}""")
            }

            // ASSERT
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }
}