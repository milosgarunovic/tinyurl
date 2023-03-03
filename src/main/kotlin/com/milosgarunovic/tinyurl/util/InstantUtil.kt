package com.milosgarunovic.tinyurl.util

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * This class represents an instant that can be modified for testing, so you can set exact moment in time, and move it
 * forwards/backwards. Use only for testing purposes. If a field in class should be tested with time (like expiring
 * date), then use this class to get an instant. If there's no need to test a field in class, use
 * java.time.Instant.now() directly.
 */
object InstantUtil {

    private var instant: Instant? = null

    fun setFixed(instant: Instant = Instant.now()) {
        this.instant = instant
    }

    /**
     * Clears fixed instant. When you call [now()] after calling this, it will return Instant.now() and not fixed time.
     */
    fun clear() {
        instant = null
    }

    fun plusDays(days: Long) {
        if (instant != null) {
            instant = instant!!.plus(days, ChronoUnit.DAYS)
        }
    }

    fun now(): Instant {
        if (instant != null) {
            return instant!!
        }
        return Instant.now()
    }

}