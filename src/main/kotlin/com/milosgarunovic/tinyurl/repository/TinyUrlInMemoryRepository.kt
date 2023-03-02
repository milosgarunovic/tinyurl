package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.toTinyUrl
import com.milosgarunovic.tinyurl.util.InstantUtil
import com.milosgarunovic.tinyurl.util.random8Chars

class TinyUrlInMemoryRepository(private val urls: MutableList<TinyUrl> = ArrayList()) {

    fun add(tinyUrlAddReq: TinyUrlAddReq): String {
        var shortUrl: String

        do {
            // generate new if it already exists
            shortUrl = random8Chars()
        } while (urls.indexOfFirst { it.shortUrl == shortUrl } != -1)

        urls.add(tinyUrlAddReq.toTinyUrl(shortUrl))

        return shortUrl
    }

    fun get(shortUrl: String): String? {
        return urls.find {
            it.shortUrl == shortUrl &&
                    it.active &&
                    it.calculatedExpiry?.isAfter(InstantUtil.now()) != false
        }?.url
    }

    fun update(shortUrl: String, url: String) {
        val index = urls.indexOfFirst { it.shortUrl == shortUrl }
        if (index != -1) {
            urls[index] = urls[index].copy(url = url)
        }
    }

    fun delete(shortUrl: String) {
        val index = urls.indexOfFirst { it.shortUrl == shortUrl }
        if (index != -1) {
            // TODO what if it's already deleted? Is it even possible to access that entity
            urls[index].deactivate()
        }
    }
}