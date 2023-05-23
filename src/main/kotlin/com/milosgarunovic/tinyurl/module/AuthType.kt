package com.milosgarunovic.tinyurl.module

enum class AuthType(val type: String) {
    JWT("jwt"),
    JWT_ADMIN("jwt-admin");
}