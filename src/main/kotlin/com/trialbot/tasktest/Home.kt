package com.trialbot.tasktest

import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SomeObject(val name: String, val index: Int)

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = ["*"], maxAge = 86400)
class Home(private val userRepository: UserRepository) {

    @GetMapping("/")
    fun index(): ResponseEntity<SomeObject> =
        ResponseEntity(SomeObject("My name", 1), HttpStatus.OK)
}