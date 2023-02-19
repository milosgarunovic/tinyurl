package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.json.Expires
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.util.random8Chars

class InMemoryRepository(private val urls: MutableMap<String, TinyUrlAddReq>) {

    fun add(tinyUrlAddReq: TinyUrlAddReq): String {
        val generatedTinyUrl = random8Chars()
        // TODO check if generatedTinyUrl already exists
        urls[generatedTinyUrl] = tinyUrlAddReq

        return generatedTinyUrl
    }

    fun get(id: String): String? {
        val tinyUrlAddReq = urls[id]
        if (tinyUrlAddReq?.expires != null) {
            val e = tinyUrlAddReq.expires
            // if expired, return null - which will return 404, as in expired or 410 - Gone
            when (e) {
                is Expires.In -> {
                }

                is Expires.At -> {
                }
            }
        }
        return urls[id]?.url
    }

    fun update(id: String, url: String) {
        val copy = urls[id]?.copy(url = url)
        urls[id] = copy!!
    }

    fun delete(id: String) {
        urls.remove(id)
    }
}