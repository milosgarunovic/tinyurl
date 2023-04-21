package com.milosgarunovic.tinyurl.entity

import com.milosgarunovic.tinyurl.json.PropertiesJson
import java.util.*

class Properties(
    val id: String = UUID.randomUUID().toString(),
    /**
     * When true, users can register. When false, registration is disabled.
     */
    private val registrationEnabled: Boolean,
    /**
     * When true, anyone can create a URL, even without account. When false only registered users can create URL.
     */
    private val publicUrlCreation: Boolean,
//    val updatedBy: User?,
//    val dateUpdated: Instant?,
) {

    fun toPropertiesJson() = PropertiesJson(registrationEnabled, publicUrlCreation)

}