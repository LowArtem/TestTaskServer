package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
    fun findByUsernameAndPassword(username: String, password: String): List<User>

    fun findByUsername(username: String): List<User>

    @Query(value = "select case when ((select count(1) from users where users.id = ?1 limit 1) = 1) then true else false end", nativeQuery = true)
    fun checkIfUserExists(userId: Int): Boolean
}