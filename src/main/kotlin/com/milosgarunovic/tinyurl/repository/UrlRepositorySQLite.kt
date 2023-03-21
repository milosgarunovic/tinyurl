package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.ext.milli
import com.milosgarunovic.tinyurl.util.InstantUtil
import java.time.Instant

class UrlRepositorySQLite : UrlRepository {

    override fun add(tinyUrl: TinyUrl, email: String?): TinyUrl {
        val expiry = tinyUrl.calculatedExpiry.milli()
        val dateCreated = tinyUrl.dateCreated.milli()
        if (email != null) {
            //language=SQLite
            val query = """INSERT INTO url (id, short_url, url, calculated_expiry, date_created, active, user_id) 
                VALUES (?, ?, ?, ?, ?, true, (SELECT id FROM users WHERE email = ?));"""
            SQLite.insert(
                query,
                1 to tinyUrl.id,
                2 to tinyUrl.shortUrl,
                3 to tinyUrl.url,
                4 to expiry,
                5 to dateCreated,
                6 to email
            )
        } else {
            //language=SQLite
            val query = """INSERT INTO url (id, short_url, url, calculated_expiry, date_created, active) 
                VALUES (?, ?, ?, ?, ?, true);"""
            SQLite.insert(
                query,
                1 to tinyUrl.id,
                2 to tinyUrl.shortUrl,
                3 to tinyUrl.url,
                4 to expiry,
                5 to dateCreated
            )
        }
        return tinyUrl
    }

    override fun getUrl(shortUrl: String): String? {
        //language=SQLite
        val query = "SELECT url, calculated_expiry FROM url WHERE short_url = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to shortUrl)
        if (resultSet.next()) {
            val expiry = resultSet.getLong("calculated_expiry")
            if (expiry != 0L && expiry < InstantUtil.now().toEpochMilli()) {
                return null
            }
            return resultSet.getString("url")
        }
        return null
    }

    override fun update(shortUrl: String, url: String, email: String): Boolean {
        //language=SQLite
        val query = "UPDATE url SET url = ? WHERE short_url = ? AND  user_id = (SELECT id FROM users WHERE email = ?);"
        return SQLite.update(query, 1 to url, 2 to shortUrl, 3 to email)
    }

    override fun delete(shortUrl: String, email: String): Boolean {
        //language=SQLite
        val query = """UPDATE url SET active = false, date_deactivated = ? 
            WHERE short_url = ? 
            AND active 
            AND user_id = (SELECT id FROM users WHERE email = ?);"""
        return SQLite.update(query, 1 to Instant.now().toEpochMilli(), 2 to shortUrl, 3 to email)
    }

    override fun exists(shortUrl: String): Boolean {
        //language=SQLite
        val query = "SELECT 1 FROM url WHERE short_url = ? AND active = 1;"
        val resultSet = SQLite.query(query, 1 to shortUrl)
        return resultSet.next()
    }
}