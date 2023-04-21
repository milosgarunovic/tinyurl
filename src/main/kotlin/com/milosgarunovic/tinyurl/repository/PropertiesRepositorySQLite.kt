package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.Properties

class PropertiesRepositorySQLite : PropertiesRepository {

    override fun getProperties(): Properties {
        val query = "SELECT id, registration_enabled, public_url_creation FROM properties"
        return SQLite.query(query) {
            next()
            return@query Properties(
                getString("id"),
                getBoolean("registration_enabled"),
                getBoolean("public_url_creation"),
            )
        }
    }

    override fun enableRegistration() {
        val query = "UPDATE properties SET registration_enabled = 1;"
        SQLite.update(query)
    }

    override fun disableRegistration() {
        val query = "UPDATE properties SET registration_enabled = 0;"
        SQLite.update(query)
    }

    override fun enablePublicUrlCreation() {
        val query = "UPDATE properties SET public_url_creation = 1;"
        SQLite.update(query)
    }

    override fun disablePublicUrlCreation() {
        val query = "UPDATE properties SET public_url_creation = 0;"
        SQLite.update(query)
    }
}