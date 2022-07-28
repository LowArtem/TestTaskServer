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
        val email = "example7@example.com"
        val password = "TrialBot"

        val userLogged: UserLoginResponse? = userAuthService.login(UserLoginRequest(email, password), authenticationProvider)
        assertNotNull(userLogged)
        assertEquals(email, userLogged?.email)
        assertEquals(password, userLogged?.password)
        assertNotNull(userLogged?.token)
        assertEquals(7, userLogged?.id)
    }

    @Test
    fun `login user that does not exist`() {
        val email = "bullshit@example.com"
        val password = "asdgasdgashfg"

        var userLogged: UserLoginResponse? = null

        assertThrows(BadCredentialsException::class.java) {
            userLogged = userAuthService.login(UserLoginRequest(email, password), authenticationProvider)
        }

        assertNull(userLogged)
    }

    @Test
    fun `register successful`() {
        val email = "brand_new_email@example.com"
        val username = "Brand new username"
        val password = "brandnewusername"

        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(
            username = username,
            email = email,
            password = password
        ))
        assertTrue(userRegistered)

        userRepo.findByEmailAndPassword(email, password).firstOrNull().let {
            assertNotNull(it)
            assertNotNull(it?.id)

            userRepo.deleteById(it!!.id!!)
        }
    }

    @Test
    fun `register user when email is already exists`() {
        val email = "example7@example.com"
        val username = "TrialBot"
        val password = "TrialBot"

        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(
            username = username,
            email = email,
            password = password
        ))
        assertFalse(userRegistered)
    }

    @Test
    fun `register user when username is already exists`() {
        val email = "this_email_doesnt_exist@example.com"
        val username = "TrialBot"
        val password = "TrialBot"

        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(
            username = username,
            email = email,
            password = password
        ))
        assertFalse(userRegistered)
    }

    @Test
    fun `register given email is not valid`() {
        val email = "this_is_invalid_email"
        val username = "a;sdlkhag;hsd;afdhga[perotyafy;ga;kjdfh"
        val password = "TrialBot"

        val userRegistered: Boolean = userAuthService.register(UserRegisterRequest(
            username = username,
            email = email,
            password = password
        ))
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