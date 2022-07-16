package com.trialbot.tasktest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

data class SomeObject(val name: String, val index: Int)

@RestController
class Home {

    @GetMapping("/")
    fun index(): ResponseEntity<SomeObject> =
        ResponseEntity(SomeObject("My name", 1), HttpStatus.OK)

    @GetMapping("/{id}")
    fun testIndex(@PathVariable id: Int): ResponseEntity<String> {
        if (id < 1 || id > 500) return ResponseEntity("Wrong number range", HttpStatus.NOT_FOUND)

        return ResponseEntity("The number is $id", HttpStatus.OK)
    }
}