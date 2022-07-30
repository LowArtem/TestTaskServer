package com.trialbot.tasktest.models.enums

import java.time.Instant
import java.util.Calendar

enum class RepeatingInterval {
    NONE,
    WEEKLY,
    MONTHLY,
    YEARLY
}

fun RepeatingInterval.toMillis(currentTime: Instant): Long = when (this) {
    RepeatingInterval.NONE -> 0
    RepeatingInterval.WEEKLY -> 7 * 24 * 60 * 60 * 1000
    RepeatingInterval.MONTHLY -> {
        val calendar = Calendar.Builder().setInstant(currentTime.toEpochMilli()).build()
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * 24 * 60 * 60 * 1000L
    }
    RepeatingInterval.YEARLY -> {
        val calendar = Calendar.Builder().setInstant(currentTime.toEpochMilli()).build()
        calendar.getActualMaximum(Calendar.DAY_OF_YEAR) * 24 * 60 * 60 * 1000L
    }
}