package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl

interface UrlRepository {

    fun add(tinyUrl: TinyUrl, email: String?): TinyUrl

    fun getUrl(shortUrl: String): String?

    fun update(shortUrl: String, url: String)

    fun delete(shortUrl: String)

    fun exists(shortUrl: String): Boolean

}