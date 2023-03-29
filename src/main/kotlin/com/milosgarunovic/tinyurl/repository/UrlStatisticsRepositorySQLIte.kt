package com.milosgarunovic.tinyurl.repository

import java.time.Instant
import java.util.*

class UrlStatisticsRepositorySQLIte : UrlStatisticsRepository {

    override fun add(shortUrl: String, userId: String) {
        //language=SQLite
        val query = "INSERT INTO url_statistics (id, date_created, short_url, user_id) VALUES (?, ?, ?, ?);"
        SQLite.insert(
            query,
            1 to UUID.randomUUID().toString(),
            2 to Instant.now().toEpochMilli(),
            3 to shortUrl,
            4 to userId,
        )
    }
}