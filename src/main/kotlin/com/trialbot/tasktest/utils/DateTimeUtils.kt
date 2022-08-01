package com.trialbot.tasktest.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Instant.toLocalDateTime(timeZone: ZoneId): LocalDateTime = LocalDateTime.ofInstant(this, timeZone)

fun Instant.toLocalDateTimeUTC(): LocalDateTime = this.toLocalDateTime(ZoneId.of("UTC"))