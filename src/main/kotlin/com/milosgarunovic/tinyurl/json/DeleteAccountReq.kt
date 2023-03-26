package com.milosgarunovic.tinyurl.json

import kotlinx.serialization.Serializable

@Serializable
class DeleteAccountReq(val confirmPassword: String)