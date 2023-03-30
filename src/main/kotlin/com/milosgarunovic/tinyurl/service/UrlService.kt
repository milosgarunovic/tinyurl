package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.exception.NotFoundException
import com.milosgarunovic.tinyurl.json.UrlAddReq
import com.milosgarunovic.tinyurl.repository.UrlRepository
import com.milosgarunovic.tinyurl.repository.UrlStatisticsRepository
import com.milosgarunovic.tinyurl.util.random8Chars
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UrlService : KoinComponent {

    private val urlRepository by inject<UrlRepository>()

    private val statisticsRepository by inject<UrlStatisticsRepository>()

    fun getUrl(shortUrl: String): String {
        val url = urlRepository.getUrl(shortUrl) ?: throw NotFoundException()

        if (url.third != null) {
            // TODO maybe save url as well because url can be updated
            statisticsRepository.add(url.second, url.third!!)
        }
        return url.first
    }

    fun add(urlAddReq: UrlAddReq, email: String?): String {
        var shortUrl: String
        do { // generate new if it already exists
            shortUrl = random8Chars()
        } while (urlRepository.exists(shortUrl))

        val url = urlAddReq.toTinyUrl(shortUrl)
        urlRepository.add(url, email)
        return url.shortUrl
    }

    fun update(shortUrl: String, url: String, email: String) {
        val isUpdated = urlRepository.update(shortUrl, url, email)
        if (!isUpdated) {
            throw NotFoundException()
        }
    }

    fun delete(shortUrl: String, email: String) {
        val isDeleted = urlRepository.delete(shortUrl, email)
        if (!isDeleted) {
            throw NotFoundException()
        }
    }

}