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

    // TODO need tests to pass with /api/tinyurl - lower case, but that fails
    val basePath = "/api/tinyUrl"

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
            val createdResponse = client.post(basePath) {
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

        @Test
        fun `GET root with deleted path - returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val createdResponse = client.post(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "https://test.com"}""")
            }
            val id = createdResponse.bodyAsText()

            client.delete("$basePath/$id")

            val response = client.get("/$id")

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Nested
    inner class PostApiTinyUrlTests {

        @Test
        fun `POST api-tinyUrl with body returns 201 and has length 8`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.post(basePath) {
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
            val createdResponse = client.post(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "https://test.com"}""")
            }

            val id = createdResponse.bodyAsText()
            val response = client.patch(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"id":"$id", "url":"https://test2.com"}""")
            }

            // ASSERT
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Nested
    inner class DeleteApiTinyUrlTest {

        @Test
        fun `DELETE api-tinyUrl - return 204`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val createdResponse = client.post(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "https://test.com"}""")
            }
            val id = createdResponse.bodyAsText()

            val response = client.delete("$basePath/$id")

            // ASSERT
            assertEquals(HttpStatusCode.NoContent, response.status)
        }
    }
}