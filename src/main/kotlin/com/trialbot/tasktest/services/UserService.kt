package com.trialbot.tasktest.services

import com.trialbot.tasktest.models.User
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired private val userRepo: UserRepository) {

    fun login(username: String, password: String): User? =
        userRepo.findByUsernameAndPassword(username, password).firstOrNull()

    fun register(username: String, password: String): User? {
        if (userRepo.findByUsername(username).isNotEmpty())
            return null

        val user = User(username = username, password = password, tasks = setOf())
        return userRepo.save(user)
    }

    fun getById(id: Int): User? = userRepo.findByIdOrNull(id)
}