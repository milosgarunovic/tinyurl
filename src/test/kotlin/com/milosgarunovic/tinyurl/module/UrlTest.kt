package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.mainModule
import com.milosgarunovic.tinyurl.util.InstantUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.ktor.server.testing.client.*
import io.ktor.util.reflect.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.ZoneId
import kotlin.time.Duration.Companion.days

class UrlTest : AbstractTest() {

    private val apiUrl = "/api/url"

    private var user1Token: String = "empty user 1 token"
    private var user2Token: String = "empty user 2 token"

    private var user1Auth = """{"email": "user1@test.com", "password": "password"}"""
    private var user2Auth = """{"email": "user2@test.com", "password": "password"}"""

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()

        // create two users
        testApplication {
            application { mainModule() }
            post(client, "/api/user/register", user1Auth)
            post(client, "/api/user/register", user2Auth)
        }
    }

    @BeforeEach
    fun beforeEach() = testApplication {
        application { mainModule() }
        val client = httpClient()
        user1Token = login(client, user1Auth)
        user2Token = login(client, user2Auth)
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
            val id = post(client, apiUrl, """{"url": "$expectedUrl"}""", user1Token).bodyAsText()

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
            val client = httpClient()

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()
            delete(client, "$apiUrl/$id", user1Token) // delete created post
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
            val id = post(client, apiUrl, """{"url": "$expectedUrl"}""", user1Token).bodyAsText()

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
            val id = post(client, apiUrl, """{"url": "$expectedUrl"}""", user1Token).bodyAsText()
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
                val url = post(client, apiUrl, reqBody, user1Token).bodyAsText()

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
            val url = post(client, apiUrl, reqBody, user1Token).bodyAsText()

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
    inner class PostApiUrlTests {

        @Test
        @DisplayName("POST /api/url with url in body returns 201 and has length 8")
        fun `POST api-tinyUrl with url in body returns 201 and has length 8`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"url": "https://test.com"}"""
            val res = post(client, apiUrl, reqBody, user1Token)

            // ASSERT
            assertEquals(HttpStatusCode.Created, res.status)
            assertEquals(8, res.bodyAsText().length)
        }

        @Test
        @DisplayName("POST /api/url without authorization returns 201")
        fun `POST api-tinyUrl without authorization returns 201`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val reqBody = """{"url": "https://test.com"}"""
            val res = post(client, apiUrl, reqBody, token = null)

            // ASSERT
            assertEquals(HttpStatusCode.Created, res.status)
        }
    }

    @Nested
    inner class PatchApiUrlTests {

        @Test
        @DisplayName("PATCH /api/url with body returns 200")
        fun `PATCH api-tinyUrl with body returns 200`() = testApplication {
            // ARRANGE
            application { mainModule() }
            val client = httpClient()
            val expectedUrl = "https://test2.com"

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()
            val res = patch(client, apiUrl, """{"id":"$id", "url":"$expectedUrl"}""", user1Token)

            // ASSERT
            assertEquals(HttpStatusCode.OK, res.status)

            val get = client.get("/$id")
            assertEquals(HttpStatusCode.MovedPermanently, get.status)
            assertEquals(expectedUrl, get.headers["Location"])
        }

        @Test
        @DisplayName("PATCH /api/url without authorization returns 401")
        fun `PATCH api-tinyUrl without authorization returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()
            val res = patch(client, apiUrl, """{"id":"$id", "url":"https://test2.com"}""", token = null)

            // TODO check logs see why it doesn't pick up 401 in status code.. must be out of scope
            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, res.status)
        }

        @Test
        @DisplayName("PATCH /api/url one user cannot modify another users url returns 404")
        fun `PATCH api-tinyUrl one user cannot modify another users url returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()

            // one user cannot modify another users url
            val res = patch(client, apiUrl, """{"id":"$id", "url":"https://test2.com"}""", user2Token)

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, res.status)
        }
    }

    @Nested
    inner class DeleteApiUrlTest {

        @Test
        @DisplayName("DELETE /api/url returns 204")
        fun `DELETE api-tinyUrl returns 204`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()

            val response = delete(client, "$apiUrl/$id", user1Token)

            // ASSERT
            assertEquals(HttpStatusCode.NoContent, response.status)
        }

        @Test
        @DisplayName("DELETE /api/url without id returns 404")
        fun `DELETE api-tinyUrl without id returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val response = delete(client, "$apiUrl/", user1Token)

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

        @Test
        @DisplayName("DELETE /api/url without basic auth returns 401")
        fun `DELETE api-tinyUrl without basic auth returns 401`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()

            val response = delete(client, "$apiUrl/$id", token = null)

            // ASSERT
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        @Test
        @DisplayName("DELETE /api/url one user cannot delete another users url returns 404")
        fun `DELETE api-tinyUrl one user cannot delete another users url returns 404`() = testApplication {
            // ARRANGE
            application { mainModule() }

            // ACT
            val id = post(client, apiUrl, """{"url": "https://test.com"}""", user1Token).bodyAsText()

            // one user cannot modify another users url
            val res = delete(client, "$apiUrl/$id", user2Token)

            // ASSERT
            assertEquals(HttpStatusCode.NotFound, res.status)
        }
    }
}