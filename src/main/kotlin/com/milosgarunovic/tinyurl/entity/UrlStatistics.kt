package com.milosgarunovic.tinyurl.entity

data class UrlStatistics(
    val shortUrl: Url,
    val user: User,
) : BaseEntity()