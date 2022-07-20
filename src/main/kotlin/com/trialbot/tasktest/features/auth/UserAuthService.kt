package com.trialbot.tasktest.features.auth

import com.trialbot.tasktest.configs.jwt.JwtUtils
import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserAuthService(
    private val userRepo: UserRepository,
) : UserDetailsService {

    fun login(loginRequest: UserLoginRequest, authenticationProvider: AuthenticationProvider): UserLoginResponse? {
        val authentication: Authentication = authenticationProvider
            .authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )
        SecurityContextHolder.getContext().authentication = authentication
        val jwtToken: String = JwtUtils.generateJwtToken(authentication)

        userRepo.findByUsernameAndPassword(loginRequest.username, loginRequest.password).firstOrNull().let { founded ->
            if (founded == null) return null

            return UserLoginResponse(
                username = founded.username,
                password = founded.password,
                token = jwtToken,
                experience = founded.experience,
                money = founded.money,
                id = founded.id
            )
        }
    }

    fun register(registerRequest: UserRegisterRequest): Boolean {
        if (userRepo.existsUserByUsername(registerRequest.username))
            return false

        val user = User(username = registerRequest.username, password = registerRequest.password, tasks = setOf())
        try {
            userRepo.save(user).toDto()
        } catch (_: Exception) {
            return false
        }
        return true
    }

    fun getById(id: Int): UserDto? = userRepo.findByIdOrNull(id)?.toDto()

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) throw UsernameNotFoundException("Username is null")
        val user = userRepo.findByUsername(username).firstOrNull()
            ?: throw UsernameNotFoundException("Username -> $username not found")

        return UserSecurityDetails(user.toDto())
    }
}