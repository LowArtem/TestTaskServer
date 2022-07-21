package com.trialbot.tasktest.utils

import org.springframework.stereotype.Component
import java.time.Instant

interface CurrentDateTimeProvider {
    fun getCurrentDateTime(): Instant

}

@Component
class CurrentDateTimeProviderDefault : CurrentDateTimeProvider {

    override fun getCurrentDateTime(): Instant = Instant.now()
}