package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq
import com.milosgarunovic.tinyurl.json.toTinyUrl
import com.milosgarunovic.tinyurl.util.random8Chars
import java.time.Instant

class UrlRepositorySQLite : UrlRepository {

    override fun add(tinyUrlAddReq: TinyUrlAddReq): TinyUrl {
        var shortUrl: String
        do { // generate new if it already exists
            shortUrl = random8Chars()
        } while (exists(shortUrl))

        val url = tinyUrlAddReq.toTinyUrl(shortUrl)

        //language=SQLite
        val query =
            "INSERT INTO url (id, shortUrl, url, calculatedExpiry, dateCreated, active) VALUES (?, ?, ?, ?, ?, true);"
        val expiry = url.calculatedExpiry?.toEpochMilli() ?: 0
        val dateCreated = url.dateCreated.toEpochMilli()
        SQLite.insert(query, 1 to url.id, 2 to url.shortUrl, 3 to url.url, 4 to expiry, 5 to dateCreated)
        return url
    }

    override fun getUrl(shortUrl: String): String? {
        //language=SQLite
        val query = "SELECT shortUrl, calculatedExpiry FROM url WHERE shortUrl = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to shortUrl)
        if (resultSet.next()) {
            val expiry = resultSet.getLong("calculatedExpiry")
            if (expiry != 0L && expiry < Instant.now().toEpochMilli()) {
                return null
            }
            return resultSet.getString("shortUrl")
        }
        return null
    }

    override fun update(shortUrl: String, url: String) {
        TODO("Not yet implemented")
    }

    override fun delete(shortUrl: String) {
        TODO("Not yet implemented")
    }

    override fun exists(shortUrl: String): Boolean {
        //language=SQLite
        val query = "SELECT 1 FROM url WHERE shortUrl = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to shortUrl)
        return resultSet.next()
    }
}