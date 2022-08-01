package com.trialbot.tasktest.models.enums

import com.trialbot.tasktest.utils.toLocalDateTimeUTC
import java.time.Instant
import java.time.ZoneOffset

enum class RepeatingInterval {
    NONE,
    WEEKLY,
    MONTHLY,
    YEARLY
}

fun RepeatingInterval.addToDate(currentTime: Instant): Instant = when(this) {
    RepeatingInterval.NONE -> currentTime
    RepeatingInterval.WEEKLY -> currentTime.toLocalDateTimeUTC().plusDays(7).toInstant(ZoneOffset.UTC)
    RepeatingInterval.MONTHLY -> currentTime.toLocalDateTimeUTC().plusMonths(1).toInstant(ZoneOffset.UTC)
    RepeatingInterval.YEARLY -> currentTime.toLocalDateTimeUTC().plusYears(1).toInstant(ZoneOffset.UTC)
}