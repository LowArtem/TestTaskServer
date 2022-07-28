package com.trialbot.tasktest.utils

fun String.validateAsEmail(): Boolean {
    val regexPattern = "^[a-zA-Z\\d_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z\\d.-]+$"
    val regex = regexPattern.toRegex()
    return regex.matches(this)
}