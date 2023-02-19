package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.toTinyUrl
import com.milosgarunovic.tinyurl.util.random8Chars
import java.time.Clock
import java.time.Instant

class InMemoryRepository(
    private val clock: Clock = Clock.systemUTC(),
    private val urls: MutableList<TinyUrl> = ArrayList()
) {

    fun add(tinyUrlAddReq: TinyUrlAddReq): String {
        var shortUrl: String

        do {
            // generate new if it already exists
            shortUrl = random8Chars()
        } while (urls.indexOfFirst { it.shortUrl == shortUrl } != -1)


        urls.add(tinyUrlAddReq.toTinyUrl(shortUrl, clock))

        return shortUrl
    }

    fun get(shortUrl: String): String? {
        val tinyUrl = urls.find { it.shortUrl == shortUrl }
        if (tinyUrl != null) {
            if (tinyUrl.calculatedExpiry != null && Instant.now(clock).isAfter(tinyUrl.calculatedExpiry)) {
                return null
            }
            return tinyUrl.url
        }
        return null
    }

    fun update(shortUrl: String, url: String) {
        val index = urls.indexOfFirst { it.shortUrl == shortUrl }
        if (index != -1) {
            urls[index] = urls[index].copy(url = url)
        }
    }

    fun delete(shortUrl: String) {
        // TODO add active attribute to search for that value
        val tinyUrl = urls.find { it.shortUrl == shortUrl }
        if (tinyUrl != null) {
            urls.remove(tinyUrl)
        }
    }
}