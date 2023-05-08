package com.milosgarunovic.tinyurl.service

import com.milosgarunovic.tinyurl.json.PropertiesJson
import com.milosgarunovic.tinyurl.repository.PropertiesRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PropertiesService : KoinComponent {

    private val propertiesRepository by inject<PropertiesRepository>()

    fun getProperties(): PropertiesJson {
        return propertiesRepository.getProperties().toPropertiesJson()
    }

    fun enableRegistration(): PropertiesJson {
        propertiesRepository.enableRegistration()
        return getProperties()
    }

    fun disableRegistration(): PropertiesJson {
        propertiesRepository.disableRegistration()
        return getProperties()
    }

    fun enablePublicUrlCreation(): PropertiesJson {
        propertiesRepository.enablePublicUrlCreation()
        return getProperties()
    }

    fun disablePublicUrlCreation(): PropertiesJson {
        propertiesRepository.disablePublicUrlCreation()
        return getProperties()
    }

    fun isPublicUrlCreationEnabled(): Boolean {
        return propertiesRepository.getProperties().publicUrlCreation
    }

    fun isRegistrationEnabled(): Boolean {
        return propertiesRepository.getProperties().registrationEnabled
    }
}