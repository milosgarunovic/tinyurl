package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.Url

interface UrlRepository {

    fun add(url: Url, email: String?): Url

    fun getUrl(shortUrl: String): String?

    fun update(shortUrl: String, url: String, email: String): Boolean

    fun delete(shortUrl: String, email: String): Boolean

    fun exists(shortUrl: String): Boolean

}