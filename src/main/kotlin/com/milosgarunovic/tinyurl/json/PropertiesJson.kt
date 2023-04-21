package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class PropertiesJson(
    val registrationEnabled: Boolean,
    val publicUrlCreation: Boolean
)