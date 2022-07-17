package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
    fun findByUsernameAndPassword(username: String, password: String): List<User>
}