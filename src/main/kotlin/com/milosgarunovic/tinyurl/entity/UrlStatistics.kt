package com.milosgarunovic.tinyurl.entity

data class UrlStatistics(
    private val url: Url,
    private val user: User,
) : BaseEntity()