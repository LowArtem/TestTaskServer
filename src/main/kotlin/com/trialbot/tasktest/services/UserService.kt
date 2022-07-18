package com.trialbot.tasktest.services

import com.trialbot.tasktest.models.User
import com.trialbot.tasktest.models.UserDto
import com.trialbot.tasktest.models.toDto
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired private val userRepo: UserRepository) {

    fun login(username: String, password: String): UserDto? =
        userRepo.findByUsernameAndPassword(username, password).firstOrNull()?.toDto()

    fun register(username: String, password: String): UserDto? {
        if (userRepo.findByUsername(username).isNotEmpty())
            return null

        val user = User(username = username, password = password, tasks = setOf())
        return userRepo.save(user).toDto()
    }

    fun getById(id: Int): UserDto? = userRepo.findByIdOrNull(id)?.toDto()
}