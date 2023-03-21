package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl

interface UrlRepository {

    fun add(tinyUrl: TinyUrl, email: String?): TinyUrl

    fun getUrl(shortUrl: String): String?

    fun update(shortUrl: String, url: String, email: String): Boolean

    fun delete(shortUrl: String, email: String): Boolean

    fun exists(shortUrl: String): Boolean

}