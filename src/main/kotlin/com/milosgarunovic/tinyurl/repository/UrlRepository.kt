package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq

interface UrlRepository {

    fun add(tinyUrlAddReq: TinyUrlAddReq, email: String): TinyUrl

    fun getUrl(shortUrl: String): String?

    fun update(shortUrl: String, url: String)

    fun delete(shortUrl: String)

    fun exists(shortUrl: String): Boolean

}