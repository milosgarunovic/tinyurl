package com.milosgarunovic.tinyurl.repository

interface UrlStatisticsRepository {

    fun add(shortUrl: String, userId: String)
}