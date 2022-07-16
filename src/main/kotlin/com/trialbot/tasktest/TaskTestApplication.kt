package com.trialbot.tasktest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class TaskTestApplication

fun main(args: Array<String>) {
    runApplication<TaskTestApplication>(*args)
}
