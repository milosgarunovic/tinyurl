package com.milosgarunovic.tinyurl.util

import org.apache.commons.lang.RandomStringUtils

class UrlPathGenerator {

    companion object {
        fun random8Chars(): String = RandomStringUtils.randomAlphanumeric(8)
    }

}