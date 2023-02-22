package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.InMemoryRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.days

class TinyUrlTest {

    // TODO need tests to pass with /api/tinyurl - lower case, but that fails
    private val basePath = "/api/tinyUrl"

    private val auth = "user" to "password"

    /**
     * Creates a http client that doesn't follow redirects.
     */
    private fun ApplicationTestBuilder.httpClient() = createClient { followRedirects = false }

    @Nested
    inner class GetRootTests {
        @Test
        @DisplayName("GET / returns 404")
        fun `GET root returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.get("/")

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        @DisplayName("GET /path returns 301")
        fun `GET root with path returns 301`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val expectedUrl = "https://test.com"

            // ACT

            // create a new url
            val id = post(client, basePath, """{"url": "$expectedUrl"}""", auth).bodyAsText()

            // get created url
            val response = client.get("/$id")

            // ASSERT
            assertEquals(HttpStatusCode.MovedPermanently, response.status)
            assertEquals(expectedUrl, response.headers["Location"])
        }

        @Test
        @DisplayName("GET /path with deleted path returns 404")
        fun `GET root with deleted path returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, basePath, """{"url": "https://test.com"}""", auth).bodyAsText()
            delete(client, "$basePath/$id", auth) // delete created post
            val response = client.get("/$id") // try to get it after deletion

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        /**
         * This tests works the same as [GET root with path returns 301]
         */
        @Test
        @DisplayName("GET /path?redirect=true returns 301")
        fun `GET root with redirect=true returns 301`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val expectedUrl = "https://test.com"

            // ACT

            // create a new post
            val id = post(client, basePath, """{"url": "$expectedUrl"}""", auth).bodyAsText()

            // get created url
            val response = client.get("/$id?redirect=true")

            // ASSERT
            assertEquals(HttpStatusCode.MovedPermanently, response.status)
            assertEquals(expectedUrl, response.headers["Location"])
        }

        @Test
        @DisplayName("GET /path?redirect=false returns 200 with body as url")
        fun `GET root with redirect=false returns 200 with body as url`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val expectedUrl = "https://test.com"

            // ACT

            // create a new post
            val id = post(client, basePath, """{"url": "$expectedUrl"}""", auth).bodyAsText()
            // get created url
            val response = client.get("/$id?redirect=false")

            // ASSERT
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedUrl, response.bodyAsText())
        }

        @Test
        @DisplayName("GET /path with expired path returns 404 - created using milliseconds")
        fun `GET root with expired path returns 404 - created using milliseconds`() {
            testApplication {
                // ARRANGE
                val clock = mockk<Clock>()
                val now = Instant.now()
                every { clock.instant() } returns now andThen now andThen now.plusMillis(2.days.inWholeMilliseconds)
                application { mainModule(InMemoryRepository(clock)) }

                val client = httpClient()
                val oneDayMillis = 1.days.inWholeMilliseconds

                // ACT
                val reqBody = """{"url": "https://test.com", "expires":{"type": "in","milliseconds": $oneDayMillis}}"""
                val url = post(client, basePath, reqBody, auth).bodyAsText()

                // ASSERT
                val get = client.get("/$url") // clock second call = now
                assertEquals(HttpStatusCode.MovedPermanently, get.status)

                val getInTwoDays = client.get("/$url") // clock third call = now + 2 days
                assertEquals(HttpStatusCode.NotFound, getInTwoDays.status)
            }
        }

        // TODO make this a parametrized test since just the json body is different from previous one
        @Test
        @DisplayName("GET /path with expired path returns 404 - created using dateTime")
        fun `GET root with expired path returns 404 - created using dateTime`() = testApplication {
            // ARRANGE
            val clock = mockk<Clock>()
            val now = Instant.now()
            every { clock.instant() } returns now andThen now andThen now.plusMillis(2.days.inWholeMilliseconds)
            application { mainModule(InMemoryRepository(clock)) }

            val client = httpClient()
            val oneDayInFuture = now.plusMillis(1.days.inWholeMilliseconds).atZone(ZoneId.of("UTC"))

            // ACT
            val reqBody = """{"url": "https://test.com", "expires":{"type": "at","dateTime": "$oneDayInFuture"}}"""
            val url = post(client, basePath, reqBody, auth).bodyAsText()

            // ASSERT
            val resNow = client.get("/$url") // clock second call = now
            assertEquals(HttpStatusCode.MovedPermanently, resNow.status)

            val resInTwoDays = client.get("/$url") // clock third call = now + 2 days
            assertEquals(HttpStatusCode.NotFound, resInTwoDays.status)
        }
    }

    @Nested
    inner class PostApiTinyUrlTests {

        @Test
        @DisplayName("POST /api/tinyUrl with url in body returns 201 and has length 8")
        fun `POST api-tinyUrl with url in body returns 201 and has length 8`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"url": "https://test.com"}"""
            val res = post(client, basePath, reqBody, auth)

            // ASSERT
            assertEquals(HttpStatusCode.Created, res.status)
            assertEquals(8, res.bodyAsText().length)
        }

        @Test
        @DisplayName("POST /api/tinyUrl withouth authorization returns 401")
        fun `POST api-tinyUrl without authorization returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"url": "https://test.com"}"""
            val res = post(client, basePath, reqBody, null)

            // TODO check logs see why it doesn't pick up 401 in status code.. must be out of scope
            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, res.status)
        }
    }

    @Nested
    inner class PatchApiTinyUrlTests {

        @Test
        @DisplayName("PATCH /api/tinyUrl with body returns 200")
        fun `PATCH api-tinyUrl with body returns 200`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val expectedUrl = "https://test2.com"

            // ACT
            val id = post(client, basePath, """{"url": "https://test.com"}""", auth).bodyAsText()
            val res = patch(client, basePath, """{"id":"$id", "url":"$expectedUrl"}""", auth)

            // ASSERT
            assertEquals(HttpStatusCode.OK, res.status)

            val get = client.get("/$id")
            assertEquals(HttpStatusCode.MovedPermanently, get.status)
            assertEquals(expectedUrl, get.headers["Location"])
        }

        @Test
        @DisplayName("PATCH /api/tinyUrl withouth authorization returns 401")
        fun `PATCH api-tinyUrl without authorization returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, basePath, """{"url": "https://test.com"}""", auth).bodyAsText()
            val res = patch(client, basePath, """{"id":"$id", "url":"https://test2.com"}""", null)

            // TODO check logs see why it doesn't pick up 401 in status code.. must be out of scope
            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, res.status)
        }
    }

    @Nested
    inner class DeleteApiTinyUrlTest {

        @Test
        @DisplayName("DELETE /api/tinyUrl returns 204")
        fun `DELETE api-tinyUrl returns 204`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, basePath, """{"url": "https://test.com"}""", auth).bodyAsText()

            val response = delete(client, "$basePath/$id", auth)

            // ASSERT
            assertEquals(HttpStatusCode.NoContent, response.status)
        }

        @Test
        @DisplayName("DELETE /api/tinyUrl without id returns 404")
        fun `DELETE api-tinyUrl without id returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = delete(client, "$basePath/", auth)

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        @DisplayName("DELETE /api/tinyUrl without basic auth returns 401")
        fun `DELETE api-tinyUrl without basic auth returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, basePath, """{"url": "https://test.com"}""", auth).bodyAsText()

            val response = delete(client, "$basePath/$id", null)

            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    private suspend fun post(
        client: HttpClient,
        path: String,
        reqBody: String,
        basicAuth: Pair<String, String>?
    ): HttpResponse = client.post(path) {
        contentType(ContentType.Application.Json)
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
        setBody(reqBody)
    }

    private suspend fun patch(
        client: HttpClient,
        path: String,
        reqBody: String,
        basicAuth: Pair<String, String>?
    ): HttpResponse = client.patch(path) {
        contentType(ContentType.Application.Json)
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
        setBody(reqBody)
    }

    private suspend fun delete(
        client: HttpClient,
        path: String,
        basicAuth: Pair<String, String>?
    ): HttpResponse = client.delete(path) {
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
    }
}