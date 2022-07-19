package com.trialbot.tasktest.controllers

import com.trialbot.tasktest.models.User
import com.trialbot.tasktest.models.UserDto
import com.trialbot.tasktest.models.toDto
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SomeObject(val name: String, val index: Int)

@RestController
@RequestMapping("/api/test")
class Home(private val userRepository: UserRepository) {

    @GetMapping("/")
    fun index(): ResponseEntity<SomeObject> =
        ResponseEntity(SomeObject("My name", 1), HttpStatus.OK)

    @GetMapping("/db")
    fun testDatabase(): ResponseEntity<List<UserDto>?> {
        val user: User = userRepository
            .findByUsernameAndPassword(username = "TrialBot", password = "TrialBot").firstOrNull()
            ?: return ResponseEntity(null, HttpStatus.NOT_FOUND)

        return ResponseEntity(user.tasks.flatMap { it -> it.task.users.map { it.user.toDto() } } , HttpStatus.OK)
    }
}