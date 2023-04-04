package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
data class ErrorWrapper(val message: String)