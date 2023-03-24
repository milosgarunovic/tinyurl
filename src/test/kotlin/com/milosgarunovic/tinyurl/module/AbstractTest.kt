package com.milosgarunovic.tinyurl.module

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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
    protected fun ApplicationTestBuilder.httpClient() = createClient { followRedirects = false }

    companion object {

        suspend fun post(
            client: HttpClient, path: String, reqBody: String, basicAuth: Pair<String, String>? = null
        ): HttpResponse = client.post(path) {
            contentType(ContentType.Application.Json)
            if (basicAuth != null) {
                basicAuth(basicAuth.first, basicAuth.second)
            }
            setBody(reqBody)
        }

        suspend fun patch(
            client: HttpClient, path: String, reqBody: String, basicAuth: Pair<String, String>? = null
        ): HttpResponse = client.patch(path) {
            contentType(ContentType.Application.Json)
            if (basicAuth != null) {
                basicAuth(basicAuth.first, basicAuth.second)
            }
            setBody(reqBody)
        }

        suspend fun delete(client: HttpClient, path: String, basicAuth: Pair<String, String>? = null): HttpResponse {
            return client.delete(path) {
                if (basicAuth != null) {
                    basicAuth(basicAuth.first, basicAuth.second)
                }
            }
        }
    }
}