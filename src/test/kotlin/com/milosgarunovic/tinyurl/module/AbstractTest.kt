package com.milosgarunovic.tinyurl.module

import com.milosgarunovic.tinyurl.json.LoginRes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.AfterEach
import org.koin.core.context.stopKoin

abstract class AbstractTest {

    @AfterEach
    fun afterEach() {
        stopKoin()
    }

    /**
     * Creates a http client that doesn't follow redirects.
     */
    protected fun ApplicationTestBuilder.httpClient() = createClient {
        install(ContentNegotiation) {
            json()
        }
        followRedirects = false
    }

    companion object {

        suspend fun post(client: HttpClient, path: String, reqBody: String, token: String? = null): HttpResponse {
            return client.post(path) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                if (token != null) {
                    bearerAuth(token)
                }
                setBody(reqBody)
            }
        }

        suspend fun login(client: HttpClient, reqBody: String): String {
            return post(client, "/login", reqBody).body<LoginRes>().accessToken
        }

        suspend fun patch(
            client: HttpClient, path: String, reqBody: String, token: String? = null
        ): HttpResponse = client.patch(path) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            if (token != null) {
                bearerAuth(token)
            }
            setBody(reqBody)
        }

        suspend fun delete(client: HttpClient, path: String, token: String? = null): HttpResponse {
            return client.delete(path) {
                if (token != null) {
                    bearerAuth(token)
                }
            }
        }
    }
}