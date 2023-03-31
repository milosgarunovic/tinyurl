package com.milosgarunovic.tinyurl.repository

interface UrlStatisticsRepository {

    fun add(url: String, shortUrl: String, userId: String)
}