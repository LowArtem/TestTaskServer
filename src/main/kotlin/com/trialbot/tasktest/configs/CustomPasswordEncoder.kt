package com.trialbot.tasktest.configs

import org.springframework.security.crypto.password.PasswordEncoder

class CustomPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence?): String = rawPassword.toString()

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean =
        encodedPassword?.contentEquals(rawPassword) ?: false
}