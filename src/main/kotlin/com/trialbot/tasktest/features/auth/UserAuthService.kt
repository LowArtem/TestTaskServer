package com.trialbot.tasktest.features.auth

import com.trialbot.tasktest.configs.jwt.JwtUtils
import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.CurrentDateTimeProvider
import com.trialbot.tasktest.utils.validateAsEmail
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
    private val currentDateTimeProvider: CurrentDateTimeProvider
) : UserDetailsService {

    fun login(loginRequest: UserLoginRequest, authenticationProvider: AuthenticationProvider): UserLoginResponse? {
        val authentication: Authentication = authenticationProvider
            .authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
                )
            )
        SecurityContextHolder.getContext().authentication = authentication
        val jwtToken: String = JwtUtils.generateJwtToken(authentication)

        userRepo.findByEmailAndPassword(loginRequest.email, loginRequest.password).firstOrNull().let { founded ->
            if (founded == null) return null

            return UserLoginResponse(
                username = founded.username,
                password = founded.password,
                token = jwtToken,
                email = founded.email,
                registrationDate = founded.registrationDate,
                level = founded.level,
                experience = founded.experience,
                money = founded.money,
                id = founded.id
            )
        }
    }

    fun register(registerRequest: UserRegisterRequest): Boolean {
        if (userRepo.existsUserByUsername(registerRequest.username))
            return false

        if (userRepo.existsUserByEmail(registerRequest.email))
            return false

        if (!registerRequest.email.validateAsEmail())
            return false

        val user = User(
            username = registerRequest.username,
            password = registerRequest.password,
            email = registerRequest.email,
            taskUsers = setOf(),
            registrationDate = currentDateTimeProvider.getCurrentDateTime()
        )
        try {
            userRepo.save(user).toDto()
        } catch (_: Exception) {
            return false
        }
        return true
    }

    fun getById(id: Int): UserDto? = userRepo.findByIdOrNull(id)?.toDto()

    /**
     * Actually loads user by email
     * P.S. really weird name for overridable function that can be use in different cases
     */
    override fun loadUserByUsername(email: String?): UserDetails {
        if (email == null) throw UsernameNotFoundException("Email is null")
        val user = userRepo.findByEmail(email).firstOrNull()
            ?: throw UsernameNotFoundException("User with email -> $email not found")

        return UserSecurityDetails(user.toDto())
    }
}