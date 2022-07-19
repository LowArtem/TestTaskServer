package com.trialbot.tasktest.utils

import com.trialbot.tasktest.configs.jwt.JwtUtils
import com.trialbot.tasktest.models.User
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull

fun String.getUserIdFromToken(): Int? = JwtUtils.getUserIdFromJwtToken(this)

fun String.getUserFromToken(userRepository: UserRepository): User? {
    val userId = this.getUserIdFromToken() ?: return null

    return userRepository.findByIdOrNull(userId)
}

