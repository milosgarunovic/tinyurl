package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.Url
import com.milosgarunovic.tinyurl.util.InstantUtil
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class UrlRepositorySQLite : UrlRepository {

    override fun add(url: Url, email: String?): Url {
        val expiry = url.expiry
        val dateCreated = url.dateCreated
        //language=SQLite
        val query = """INSERT INTO urls (id, short_url, url, expiry, date_created, active, user_id) 
                VALUES (?, ?, ?, ?, ?, true, (SELECT id FROM users WHERE email = ?));"""
        SQLite.insert(
            query,
            1 to url.id,
            2 to url.shortUrl,
            3 to url.url,
            4 to expiry,
            5 to dateCreated,
            6 to email
        )
        return url
    }

    /**
     * @return triple of url, shortUrl and userId
     */
    override fun getUrl(shortUrl: String): Triple<String, String, String?>? {
        //language=SQLite
        val query = """SELECT url, short_url, expiry, user_id
            | FROM urls u 
            | WHERE short_url = ? 
            | AND active = 1
            | AND (SELECT active FROM users WHERE user_id = u.user_id) = 1;""".trimMargin()
        val resultSet = SQLite.query(query, 1 to shortUrl)
        if (resultSet.next()) {
            val expiryAsString = resultSet.getString("expiry")
            if (expiryAsString != null) {
                val expiry = ZonedDateTime.parse(expiryAsString)
                if (expiry.isBefore(InstantUtil.now().atZone(ZoneId.of("UTC")))) {
                    // TODO deactivate when link expired and mark field expired maybe?
                    return null
                }
            }

            return Triple(
                resultSet.getString("url"),
                resultSet.getString("short_url"),
                resultSet.getString("user_id")
            )
        }
        return null
    }

    override fun update(shortUrl: String, url: String, email: String): Boolean {
        //language=SQLite
        val query = "UPDATE urls SET url = ? WHERE short_url = ? AND user_id = (SELECT id FROM users WHERE email = ?);"
        return SQLite.update(query, 1 to url, 2 to shortUrl, 3 to email)
    }

    override fun delete(shortUrl: String, email: String): Boolean {
        //language=SQLite
        val query = """UPDATE urls SET active = false, date_deactivated = ? 
            WHERE short_url = ? 
            AND active 
            AND user_id = (SELECT id FROM users WHERE email = ?);"""
        return SQLite.update(query, 1 to Instant.now(), 2 to shortUrl, 3 to email)
    }

    override fun exists(shortUrl: String): Boolean {
        //language=SQLite
        val query = "SELECT 1 FROM urls WHERE short_url = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to shortUrl)
        return resultSet.next()
    }
}