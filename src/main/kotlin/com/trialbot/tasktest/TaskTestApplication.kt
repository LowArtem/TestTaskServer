package com.trialbot.tasktest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskTestApplication

fun main(args: Array<String>) {
    runApplication<TaskTestApplication>(*args)
}
