package com.milosgarunovic.tinyurl.repository

import com.milosgarunovic.tinyurl.entity.Properties

interface PropertiesRepository {

    fun getProperties(): Properties

    fun enableRegistration()

    fun disableRegistration()

    fun enablePublicUrlCreation()

    fun disablePublicUrlCreation()

}