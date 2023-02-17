package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.util.random8Chars

class InMemoryRepository(private val urls: MutableMap<String, String>) {

    fun add(url: String): String {
        val generatedTinyUrl = random8Chars()
        // TODO check if generatedTinyUrl already exists
        urls[generatedTinyUrl] = url
        return generatedTinyUrl
    }

    fun get(id: String): String? {
        return urls[id]
    }

    fun update(id: String, url: String) {
        urls[id] = url
    }

    fun delete(id: String) {
        urls.remove(id)
    }
}