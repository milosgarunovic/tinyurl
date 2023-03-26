package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.repository.UrlRepository
import com.milosgarunovic.tinyurl.util.random8Chars
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UrlService : KoinComponent {

    private val urlRepository by inject<UrlRepository>()

    fun getUrl(shortUrl: String): String? {
        // TODO add statistics for that url if it's not null
        return urlRepository.getUrl(shortUrl)
    }

    fun add(tinyUrlAddReq: TinyUrlAddReq, email: String?): String {
        var shortUrl: String
        do { // generate new if it already exists
            shortUrl = random8Chars()
        } while (urlRepository.exists(shortUrl))

        val url = tinyUrlAddReq.toTinyUrl(shortUrl)
        urlRepository.add(url, email)
        return url.shortUrl
    }

    fun update(shortUrl: String, url: String, email: String): Boolean {
        return urlRepository.update(shortUrl, url, email)
    }

    fun delete(shortUrl: String, email: String): Boolean {
        return urlRepository.delete(shortUrl, email)
    }

}