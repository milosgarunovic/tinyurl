package com.milosgarunovic.tinyurl.repository

import java.util.*

class InMemoryRepository(private val urls: MutableMap<String, String>) {

    fun add(url: String): String {
        val generatedTinyUrl = UUID.randomUUID().toString()
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