package com.milosgarunovic.tinyurl.entity

class User(
    val email: String,
    val password: String, // TODO set method should hash the value
) : BaseEntity()