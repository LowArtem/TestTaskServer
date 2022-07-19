package com.trialbot.tasktest.utils

import org.springframework.util.StringUtils

fun String.getToken(): String? {
    return if (StringUtils.hasText(this) && this.startsWith("Bearer ")) {
        this.substring(7, this.length)
    } else null
}