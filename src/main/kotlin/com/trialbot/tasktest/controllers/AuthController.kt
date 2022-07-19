package com.trialbot.tasktest.controllers

import com.trialbot.tasktest.models.UserLoginRequest
import com.trialbot.tasktest.models.UserLoginResponse
import com.trialbot.tasktest.models.UserRegisterRequest
import com.trialbot.tasktest.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationProvider: AuthenticationProvider
) {

    @PostMapping("/login")
    fun authUser(@RequestBody loginRequest: UserLoginRequest): ResponseEntity<*> {
        val userLogged: UserLoginResponse = userService.login(loginRequest, authenticationProvider)
            ?: return ResponseEntity.badRequest().body("User with this credentials not found")

        return ResponseEntity.ok().body(userLogged)
    }

    @PostMapping("/register")
    fun addUser(@RequestBody registerRequest: UserRegisterRequest): ResponseEntity<String> {
        val result = userService.register(registerRequest)

        return if (result) {
            ResponseEntity.ok().body("User has been successfully registered")
        } else {
            ResponseEntity.badRequest().body("This username already exists")
        }
    }
}