package com.trialbot.tasktest.features.auth

import com.trialbot.tasktest.models.UserLoginRequest
import com.trialbot.tasktest.models.UserLoginResponse
import com.trialbot.tasktest.models.UserRegisterRequest
import com.trialbot.tasktest.utils.StringResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AuthController(
    private val userAuthService: UserAuthService,
    private val authenticationProvider: AuthenticationProvider
) {

    @PostMapping("/login")
    fun authUser(@RequestBody loginRequest: UserLoginRequest): ResponseEntity<*> {
        val userLogged: UserLoginResponse = userAuthService.login(loginRequest, authenticationProvider)
            ?: return ResponseEntity.badRequest().body("User with this credentials not found")

        return ResponseEntity.ok().body(userLogged)
    }

    @PostMapping("/register")
    fun addUser(@RequestBody registerRequest: UserRegisterRequest): ResponseEntity<StringResponse> {
        val result = userAuthService.register(registerRequest)

        return if (result) {
            ResponseEntity.ok().body(StringResponse("User has been successfully registered"))
        } else {
            ResponseEntity.badRequest().body(StringResponse("This username already exists"))
        }
    }
}