package com.milosgarunovic.tinyurl.entity

data class User(
    val email: String,
    val password: String,
//    val isAdmin: Boolean, // for now it's only a field in DB and it's not exposed
) : BaseEntity()