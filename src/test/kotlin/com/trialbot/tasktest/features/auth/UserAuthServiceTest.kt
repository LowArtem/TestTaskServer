package com.trialbot.tasktest.features.auth

import com.trialbot.tasktest.models.UserLoginRequest
import com.trialbot.tasktest.models.UserLoginResponse
import com.trialbot.tasktest.models.UserRegisterRequest
import com.trialbot.tasktest.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException

@SpringBootTest
internal class UserAuthServiceTest(
    @Autowired private val userRepo: UserRepository,
    @Autowired private val userAuthService: UserAuthService,
    @Autowired private val authenticationProvider: AuthenticationProvider
) {

    @Test
    fun `login successful`() {
        val username = "TrialBot"
        val password = "TrialBot"

        val userLogged: UserLoginResponse? = userAuthService.login(UserLoginRequest(username, password), authenticationProvider)
        assertNotNull(userLogged)
        assertEquals(username, userLogged?.username)
        assertEquals(password, userLogged?.password)
        assertNotNull(userLogged?.token)
        assertEquals(7, userLogged?.id)
    }

    @Test
    fun `login user that does not exist`() {
        val username = "Bullshit"
        val password = "asdgasdgashfg"

        var userLogged: UserLoginResponse? = null

        assertThrows(BadCredentialsException::class.java) {
            userLogged = userAuthService.login(UserLoginRequest(username, password), authenticationProvider)
        }

        assertNull(userLogged)
    }

    @Test
    fun `register successful`() {
        val username = "Brand new username"
        val password = "brandnewusername"

        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(username, password))
        assertTrue(userRegistered)

        userRepo.findByUsernameAndPassword(username, password).firstOrNull().let {
            assertNotNull(it)
            assertNotNull(it?.id)

            userRepo.deleteById(it!!.id!!)
        }
    }

    @Test
    fun `register user that already exist`() {
        val username = "TrialBot"
        val password = "TrialBot"
        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(username, password))
        assertFalse(userRegistered)
    }

    @Test
    fun `getById successful`() {
        val user = userAuthService.getById(7)
        assertNotNull(user)
        assertEquals(7, user?.id)
        assertEquals("TrialBot", user?.username)
    }

    @Test
    fun `getById id does not exist`() {
        val user = userAuthService.getById(60002)
        assertNull(user)
    }
}