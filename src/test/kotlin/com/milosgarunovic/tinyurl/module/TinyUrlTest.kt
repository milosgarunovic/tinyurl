package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

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
            val expectedUrl = "https://test.com"

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
                // language=json
                setBody("""{"url": "https://test.com"}""")
            }
            val id = createdResponse.bodyAsText()

            client.delete("$basePath/$id")

            val response = client.get("/$id")

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        fun `GET root with redirect=true - returns 301 (works same as without query parameter)`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // custom client that doesn't follow redirects
            val client = createClient {
                followRedirects = false
            }
            val expectedUrl = "https://test.com"

            // ACT

            // create a new post
            val createdResponse = client.post(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "$expectedUrl"}""")
            }
            val id = createdResponse.bodyAsText()

            // get created url
            val response = client.get("/$id?redirect=true")

            // ASSERT
            assertEquals(HttpStatusCode.MovedPermanently, response.status)
            assertEquals(expectedUrl, response.headers["Location"])
        }

        @Test
        fun `GET root with redirect=false - returns 200 with body as url`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // custom client that doesn't follow redirects
            val client = createClient {
                followRedirects = false
            }
            val expectedUrl = "https://test.com"

            // ACT

            // create a new post
            val createdResponse = client.post(basePath) {
                contentType(ContentType.Application.Json)
                setBody("""{"url": "$expectedUrl"}""")
            }
            val id = createdResponse.bodyAsText()

            // get created url
            val response = client.get("/$id?redirect=false")

            // ASSERT
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedUrl, response.bodyAsText())
        }
    }

    @Nested
    inner class PostApiTinyUrlTests {

        @Test
        fun `POST api-tinyUrl with url returns 201 and has length 8`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.post(basePath) {
                contentType(ContentType.Application.Json)
                // language=json
                setBody("""{"url": "https://test.com"}""")
            }

            // ASSERT
            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals(8, response.bodyAsText().length)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Disabled("feature not implemented yet")
        @Test
        fun `POST api-tinyUrl with url and expires returns 201 and has length 8`() = runTest {

            testApplication {
                // ARRANGE
                application { mainModule() }
                val client = createClient {
                    followRedirects = false
                    developmentMode = true // advanced stacktrace
                }

                // ACT
                val response = client.post(basePath) {
                    contentType(ContentType.Application.Json)
                    // language=json
                    setBody("""{"url": "https://test.com", "expires":{"type": "in","days": 1}}""")
                }

                // ASSERT
                assertEquals(HttpStatusCode.Created, response.status)
                val url = response.bodyAsText()
                assertEquals(8, url.length)

                // ACT
                val get = client.get("/$url")
                assertEquals(HttpStatusCode.MovedPermanently, get.status)

                advanceTimeBy(2.days.inWholeMilliseconds)

                val getInTwoDays = client.get("/$url")
                assertEquals(HttpStatusCode.NotFound, getInTwoDays.status)
            }
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
                // language=json
                setBody("""{"url": "https://test.com"}""")
            }

            val id = createdResponse.bodyAsText()
            val response = client.patch(basePath) {
                contentType(ContentType.Application.Json)
                // language=json
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
                // language=json
                setBody("""{"url": "https://test.com"}""")
            }
            val id = createdResponse.bodyAsText()

            val response = client.delete("$basePath/$id")

            // ASSERT
            assertEquals(HttpStatusCode.NoContent, response.status)
        }

        @Test
        fun `DELETE api-tinyUrl without id - returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.delete("$basePath/")

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}