package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.util.UrlPathGenerator

class InMemoryRepository(private val urls: MutableMap<String, String>) {

    fun add(url: String): String {
        val generatedTinyUrl = UrlPathGenerator.random8Chars()
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

    fun print() {
        urls.forEach { (short, long) -> println("short: $short, long $long") }
    }
}