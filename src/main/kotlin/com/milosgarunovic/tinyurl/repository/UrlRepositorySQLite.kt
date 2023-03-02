package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.TinyUrl
import com.milosgarunovic.tinyurl.ext.toInstant
import com.milosgarunovic.tinyurl.json.TinyUrlAddReq

class UrlRepositorySQLite : UrlRepository {
    override fun add(tinyUrlAddReq: TinyUrlAddReq): TinyUrl {

        TODO("Not yet implemented")
    }

    override fun get(shortUrl: String): TinyUrl? {
        //language=SQLite
        val query = "SELECT * FROM url WHERE shortUrl = ? AND active = 1;"

        val resultSet = SQLite.query(query, 1 to shortUrl)
        return if (resultSet.next()) {
            TinyUrl(
                resultSet.getString("shortUrl"),
                resultSet.getString("url"),
                resultSet.getLong("shortUrl").toInstant(),
            ).apply {
                id = resultSet.getString("id")
                dateCreated = resultSet.getLong("dateCreated").toInstant()
            }
        } else null
    }

    override fun getUrl(shortUrl: String): String? {
        //language=SQLite
        val query = "SELECT * FROM url WHERE shortUrl = ? AND active = 1;"

        val resultSet = SQLite.query(query, 1 to shortUrl)
        return if (resultSet.next()) {
            resultSet.getString("shortUrl")
        } else null
    }

    override fun update(shortUrl: String, url: String) {
        TODO("Not yet implemented")
    }

    override fun delete(shortUrl: String) {
        TODO("Not yet implemented")
    }


}