package com.milosgarunovic.tinyurl.entity

data class User(
    val email: String,
    val password: String,
) : BaseEntity()