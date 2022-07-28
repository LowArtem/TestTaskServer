package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
    fun findByEmailAndPassword(email: String, password: String): List<User>

    fun findByEmail(email: String): List<User>

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean

    fun existsUserById(id: Int): Boolean
}