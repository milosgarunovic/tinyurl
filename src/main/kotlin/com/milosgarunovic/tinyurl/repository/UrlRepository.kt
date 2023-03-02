package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq

interface UrlRepository {

    fun add(tinyUrlAddReq: TinyUrlAddReq): TinyUrl

    fun get(shortUrl: String): TinyUrl?

    fun update(shortUrl: String, url: String)

    fun delete(shortUrl: String)

}