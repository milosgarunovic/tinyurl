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

        // statistics are written only if an active user created that URL
        if (url.third != null) {
            statisticsRepository.add(url.first, url.second, url.third!!)
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
//        val resultingUrl = getUrl(shortUrl)
//        println(resultingUrl)
        return url.shortUrl
    }

    /**
     * The point of updating a URL is for example if a user uses short URL on some of his websites to redirect to his
     * website, and then he changes how URLs look like or something like that, he still wants to leave the short URL
     * as is, but just update the url that user is being redirected to.
     */
    fun update(shortUrl: String, url: String, email: String) {
        val isUpdated = urlRepository.update(shortUrl, url, email)
        if (!isUpdated) {
            throw NotFoundException()
        }
    }

    /**
     * When user deletes a URL, statistics are also deleted.
     */
    fun delete(shortUrl: String, email: String) {
        val isDeleted = urlRepository.delete(shortUrl, email)
        statisticsRepository.deleteByShortUrl(shortUrl)
        if (!isDeleted) {
            throw NotFoundException()
        }
    }

}