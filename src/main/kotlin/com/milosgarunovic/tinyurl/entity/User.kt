package com.milosgarunovic.tinyurl.entity

class User(
    val username: String,
    val email: String,
    val password: String, // set method should hash the value
) : BaseEntity()