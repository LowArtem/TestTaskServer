package com.trialbot.tasktest.services

import com.trialbot.tasktest.models.UserDto
import com.trialbot.tasktest.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class UserServiceTest(
    @Autowired private val userRepo: UserRepository
) {

    private val userService = UserService(userRepo)

    @Test
    fun `login successful`() {
        val username = "TrialBot"
        val password = "TrialBot"

        val userLogged: UserDto? = userService.login(username, password)
        assertNotNull(userLogged)
        assertEquals(username, userLogged?.username)
        assertEquals(password, userLogged?.password)
        assertEquals(7, userLogged?.id)
    }

    @Test
    fun `login user that does not exist`() {
        val username = "Bullshit"
        val password = "asdgasdgashfg"

        val userLogged: UserDto? = userService.login(username, password)
        assertNull(userLogged)
    }

    @Test
    fun `register successful`() {
        val username = "Brand new username"
        val password = "brandnewusername"

        val userRegistered = userService.register(username, password)
        assertNotNull(userRegistered)

        userRepo.deleteById(userRegistered?.id!!)
    }

    @Test
    fun `register user that already exist`() {
        val username = "TrialBot"
        val password = "TrialBot"
        val userRegistered = userService.register(username, password)
        assertNull(userRegistered)
    }

    @Test
    fun `getById successful`() {
        val user = userService.getById(7)
        assertNotNull(user)
        assertEquals(7, user?.id)
        assertEquals("TrialBot", user?.username)
    }

    @Test
    fun `getById id does not exist`() {
        val user = userService.getById(60002)
        assertNull(user)
    }

    @Test
    fun `checkUserIfNotExists user exists`() {
        val result = userRepo.checkIfUserExists(7)
        assertTrue(result)
    }

    @Test
    fun `checkUserIfNotExists user does not exist`() {
        val result = userRepo.checkIfUserExists(765457)
        assertFalse(result)
    }
}