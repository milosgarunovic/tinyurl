package com.milosgarunovic.tinyurl.repository

import java.time.Instant
import java.util.*

class UrlStatisticsRepositorySQLIte : UrlStatisticsRepository {

    override fun add(url: String, shortUrl: String, userId: String) {
        //language=SQLite
        val query = "INSERT INTO url_statistics (id, date_created, url, short_url, user_id) VALUES (?, ?, ?, ?, ?);"
        SQLite.insert(
            query,
            1 to UUID.randomUUID().toString(),
            2 to Instant.now().toEpochMilli(),
            3 to url,
            4 to shortUrl,
            5 to userId,
        )
    }

    override fun deleteByShortUrl(shortUrl: String) {
        //language=SQLite
        val query = "UPDATE url_statistics SET active = 0, date_deactivated = ? WHERE short_url = ?;"
        SQLite.update(query, 1 to shortUrl, 2 to Instant.now().toEpochMilli())
    }
}