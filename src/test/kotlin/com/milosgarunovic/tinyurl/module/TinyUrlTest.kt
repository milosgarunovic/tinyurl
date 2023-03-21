package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.repository.SQLite
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.core.context.stopKoin
import java.time.ZoneId
import kotlin.time.Duration.Companion.days

class TinyUrlTest {

    // TODO need tests to pass with /api/tinyurl - lower case, but that fails
    private val apiTinyUrl = "/api/tinyUrl"


    /**
     * Creates a http client that doesn't follow redirects.
     */
    private fun ApplicationTestBuilder.httpClient() = createClient { followRedirects = false }

    companion object {

        private var auth = "test@test.com" to "password"

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            SQLite.setupInMemory()
//            SQLite.setup("tinyUrl") // used for debugging

            // create a user
            testApplication {
                application { mainModule() }

                client.post("/api/user/register") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"email": "${auth.first}", "password": "${auth.second}"}""")
                }
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            SQLite.close()
        }
    }

    @AfterEach
    fun afterEach() {
        stopKoin()
    }

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

        /**
         * We only generate 8 character paths at this point. When that changes, we will expand to support 9, 10 etc.
         */
        @Test
        @DisplayName("GET /path with 9 chars long path returns 404")
        fun `GET root with path 9 chars long returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = client.get("/123456789")

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
            val id = post(client, apiTinyUrl, """{"url": "$expectedUrl"}""", auth).bodyAsText()

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
            val id = post(client, apiTinyUrl, """{"url": "https://test.com"}""", auth).bodyAsText()
            delete(client, "$apiTinyUrl/$id", auth) // delete created post
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
            val id = post(client, apiTinyUrl, """{"url": "$expectedUrl"}""", auth).bodyAsText()

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
            val id = post(client, apiTinyUrl, """{"url": "$expectedUrl"}""", auth).bodyAsText()
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
                InstantUtil.setFixed()
                application { mainModule() }

                val client = httpClient()
                val oneDayMillis = 1.days.inWholeMilliseconds

                // ACT
                val reqBody = """{"url": "https://test.com", "expires":{"type": "in","milliseconds": $oneDayMillis}}"""
                val url = post(client, apiTinyUrl, reqBody, auth).bodyAsText()

                // ASSERT
                val get = client.get("/$url")
                assertEquals(HttpStatusCode.MovedPermanently, get.status)

                InstantUtil.plusDays(2)
                val getInTwoDays = client.get("/$url")
                assertEquals(HttpStatusCode.NotFound, getInTwoDays.status)
                InstantUtil.clear()
            }
        }

        // TODO make this a parametrized test since just the json body is different from previous one
        @Test
        @DisplayName("GET /path with expired path returns 404 - created using dateTime")
        fun `GET root with expired path returns 404 - created using dateTime`() = testApplication {
            // ARRANGE
            InstantUtil.setFixed()
            application { mainModule() }

            val client = httpClient()
            val oneDayInFuture = InstantUtil.now().plusMillis(1.days.inWholeMilliseconds).atZone(ZoneId.of("UTC"))

            // ACT
            val reqBody = """{"url": "https://test.com", "expires":{"type": "at","dateTime": "$oneDayInFuture"}}"""
            val url = post(client, apiTinyUrl, reqBody, auth).bodyAsText()

            // ASSERT
            val resNow = client.get("/$url")
            assertEquals(HttpStatusCode.MovedPermanently, resNow.status)

            InstantUtil.plusDays(2)
            val resInTwoDays = client.get("/$url")
            assertEquals(HttpStatusCode.NotFound, resInTwoDays.status)
            InstantUtil.clear()
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
            val res = post(client, apiTinyUrl, reqBody, auth)

            // ASSERT
            assertEquals(HttpStatusCode.Created, res.status)
            assertEquals(8, res.bodyAsText().length)
        }

        @Test
        @DisplayName("POST /api/tinyUrl withouth authorization returns 201")
        fun `POST api-tinyUrl without authorization returns 201`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"url": "https://test.com"}"""
            val res = post(client, apiTinyUrl, reqBody, null)

            // ASSERT
            assertEquals(HttpStatusCode.Created, res.status)
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
            val id = post(client, apiTinyUrl, """{"url": "https://test.com"}""", auth).bodyAsText()
            val res = patch(client, apiTinyUrl, """{"id":"$id", "url":"$expectedUrl"}""", auth)

            // ASSERT
            assertEquals(HttpStatusCode.OK, res.status)

            val get = client.get("/$id")
            assertEquals(HttpStatusCode.MovedPermanently, get.status)
            assertEquals(expectedUrl, get.headers["Location"])
        }

        @Test
        @DisplayName("PATCH /api/tinyUrl without authorization returns 401")
        fun `PATCH api-tinyUrl without authorization returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiTinyUrl, """{"url": "https://test.com"}""", auth).bodyAsText()
            val res = patch(client, apiTinyUrl, """{"id":"$id", "url":"https://test2.com"}""", null)

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
            val id = post(client, apiTinyUrl, """{"url": "https://test.com"}""", auth).bodyAsText()

            val response = delete(client, "$apiTinyUrl/$id", auth)

            // ASSERT
            assertEquals(HttpStatusCode.NoContent, response.status)
        }

        @Test
        @DisplayName("DELETE /api/tinyUrl without id returns 404")
        fun `DELETE api-tinyUrl without id returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = delete(client, "$apiTinyUrl/", auth)

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        @DisplayName("DELETE /api/tinyUrl without basic auth returns 401")
        fun `DELETE api-tinyUrl without basic auth returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiTinyUrl, """{"url": "https://test.com"}""", auth).bodyAsText()

            val response = delete(client, "$apiTinyUrl/$id", null)

            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    private suspend fun post(
        client: HttpClient, path: String, reqBody: String, basicAuth: Pair<String, String>?
    ): HttpResponse = client.post(path) {
        contentType(ContentType.Application.Json)
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
        setBody(reqBody)
    }

    private suspend fun patch(
        client: HttpClient, path: String, reqBody: String, basicAuth: Pair<String, String>?
    ): HttpResponse = client.patch(path) {
        contentType(ContentType.Application.Json)
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
        setBody(reqBody)
    }

    private suspend fun delete(
        client: HttpClient, path: String, basicAuth: Pair<String, String>?
    ): HttpResponse = client.delete(path) {
        if (basicAuth != null) {
            basicAuth(basicAuth.first, basicAuth.second)
        }
    }
}