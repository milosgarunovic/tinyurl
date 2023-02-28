package com.milosgarunovic.tinyurl.entity

class User(
    val username: String,
    val email: String,
    val password: String, // TODO set method should hash the value
) : BaseEntity()