package com.trialbot.tasktest.features.crud.habit

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.HabitCompletionRepository
import com.trialbot.tasktest.repositories.HabitRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.getUserIdFromToken
import io.jsonwebtoken.MalformedJwtException
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import javax.persistence.EntityNotFoundException


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class HabitServiceTest(
    @Autowired private val habitRepo: HabitRepository,
    @Autowired private val habitCompletionRepo: HabitCompletionRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val habitService: HabitService
) {

    private var newHabitId: Int = -1
    private var newCompletionId: Int = -1

    private val user: User
    private val newHabit: Habit

    // около 23:00 19.07 был создан
    private val authToken = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjcsImlhdCI6MTY1ODI2NTM0NSwiZXhwIjoxNjYwODk1MTQ1fQ.reeYtxMpJ-sytAy9vVI8Q5ISqlYgeP0pk44tRn-fxZMZDF4jSFxmZBGnLlktMdkjPckngbjpGDWIGacogSS0WQ"
    private val authTokenFake = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjcsImlhdCI6MTvbODI2NTrtNSwiZXhwIjoxNjYwODk1MTQ1fQ.reeYtxMpJ-sytll9vVI8Q5ISqlYgeP66k44tRn-fxZMZDF4jSFxm89GnLlktMdkjPckngbjpGDWIGacogSS0WQ"


    init {
        val userId = 7

        user = userRepo.findByIdOrNull(userId) ?: throw EntityNotFoundException()

        newHabit = Habit(
            name = "Testing habit",
            category = "Entertainment",
            user = user,
            type = 1,
            description = "AZAZAZAZAZAZAZAZAZAZAZAZAZAZAZAZAZ",
            difficulty = 3
        )
    }

    @BeforeEach
    internal fun setUp() {
        val habitDb = Habit(
            name = "Brand new habit",
            category = "Health",
            user = user,
            type = 0,
            description = "test habit for testing",
            difficulty = 2
        )
        val savedHabit = habitRepo.save(habitDb)
        newHabitId = savedHabit.id!!

        val completion = HabitCompletion(Instant.now(), savedHabit)
        newCompletionId = habitCompletionRepo.save(completion).id!!
    }

    @AfterEach
    internal fun tearDown() {
        try {
            habitRepo.deleteById(newHabitId)
            habitCompletionRepo.deleteById(newCompletionId)
        } catch (_: Exception) {
            return
        }
    }

    @Test
    fun `setUp successfulness testing`() {
        val habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)
        assertEquals("Brand new habit", habit?.name)
    }


    @Test
    fun `getHabitsByUser successful by auth token`() {
        var habits: List<HabitResponseDto> = listOf()

        assertDoesNotThrow {
            habits = habitService.getHabitsByUser(authToken)
        }
        assertEquals(43, habits.size)

        val habitIdExpected = 55
        val habitNameExpected = "Cash Manager"
        val habitDescriptionExpected = "kSREMCw34bEaZOG"
        val habitCategoryExpected = "Education"
        val habitTypeExpected = 2

        val habitTest = habits.find { it.id == habitIdExpected }

        assertNotNull(habitTest)
        assertEquals(habitIdExpected, habitTest!!.id)
        assertEquals(habitNameExpected, habitTest.name)
        assertEquals(habitDescriptionExpected, habitTest.description)
        assertEquals(habitCategoryExpected, habitTest.category)
        assertEquals(habitTypeExpected, habitTest.type)
    }

    @Test
    fun `getHabitsByUser successful (clean test, without token)`() {
        var habits: List<HabitResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "2"
        every {
            tokenMock.getUserIdFromToken()
        } returns 7

        assertDoesNotThrow {
            habits = habitService.getHabitsByUser(tokenMock)
        }
        assertEquals(43, habits.size)

        val habitIdExpected = 55
        val habitNameExpected = "Cash Manager"
        val habitDescriptionExpected = "kSREMCw34bEaZOG"
        val habitCategoryExpected = "Education"
        val habitTypeExpected = 2

        val habitTest = habits.find { it.id == habitIdExpected }

        assertNotNull(habitTest)
        assertEquals(habitIdExpected, habitTest!!.id)
        assertEquals(habitNameExpected, habitTest.name)
        assertEquals(habitDescriptionExpected, habitTest.description)
        assertEquals(habitCategoryExpected, habitTest.category)
        assertEquals(habitTypeExpected, habitTest.type)
    }

    @Test
    fun `getHabitsByUser habits empty`() {
        var habits: List<HabitResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "2"

        every {
            tokenMock.getUserIdFromToken()
        } returns 2

        assertDoesNotThrow {
            val habitTest = habitService.getHabitsByUser(tokenMock)
            habits = habitTest
        }
        assertNotNull(habits)
        assertEquals(0, habits.size)

        verify {
            tokenMock.getUserIdFromToken()
        }
    }

    @Test
    fun `addHabit successful`() {
        var habitAdded: HabitResponseDto? = null

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "2"

        every {
            tokenMock.getUserIdFromToken()
        } returns 7

        assertDoesNotThrow {
            habitAdded = habitService.addHabit(
                tokenMock,
                HabitReceiveDto(newHabit.name, newHabit.category, newHabit.type, newHabit.description, newHabit.difficulty)
            )
        }
        assertNotNull(habitAdded)
        habitAdded?.let { habit ->
            assertNotNull(habit.id)
            assertThat(habit.id).isGreaterThan(0)
            assertEquals(newHabit.name, habit.name)
            assertEquals(newHabit.description, habit.description)
            assertEquals(newHabit.category, habit.category)

            habitRepo.deleteById(habit.id)
        }
    }

    @Test
    fun `addHabit token error`() {
        assertThrows(MalformedJwtException::class.java) {
            habitService.addHabit(
                authTokenFake,
                HabitReceiveDto(newHabit.name, newHabit.category, newHabit.type, newHabit.description, newHabit.difficulty)
            )
        }
    }

    @Test
    fun `addHabit user not found`() {
        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "2"
        every {
            tokenMock.getUserIdFromToken()
        } returns 634576

        assertThrows(EntityNotFoundException::class.java) {
            habitService.addHabit(
                tokenMock,
                HabitReceiveDto(newHabit.name, newHabit.category, newHabit.type, newHabit.description, newHabit.difficulty)
            )
        }
    }

    @Test
    fun `updateHabit successful`() {
        val habit = habitRepo.findByIdOrNull(newHabitId) ?: throw EntityNotFoundException()
        assertNotNull(habit)

        val newHabitDto = habit.toResponseDto()
        newHabitDto.description = "ja;slkdfaj;sga;jfglfglaa[dpof"

        var habitUpdated: HabitResponseDto? = null
        assertDoesNotThrow {
            habitUpdated = habitService.updateHabit(newHabitDto)
        }
        assertNotNull(habitUpdated)
        habitUpdated?.let { updated ->
            assertEquals(newHabitDto.description, updated.description)
            assertEquals("Brand new habit", updated.name)
            assertEquals(newHabitId, updated.id)
        }
    }

    @Test
    fun `updateHabit habit doesn't exist`() {
        val habit = habitRepo.findByIdOrNull(newHabitId) ?: throw EntityNotFoundException()
        assertNotNull(habit)

        val newHabitDto = habit.toResponseDto()
        newHabitDto.description = "ja;slkdfaj;sga;jfglfglaa[dpof"
        val habitDto = newHabitDto.copy(id = 827364)

        var habitUpdated: HabitResponseDto? = null
        assertThrows(EntityNotFoundException::class.java) {
            habitUpdated = habitService.updateHabit(habitDto)
        }
        assertNull(habitUpdated)
    }


    @Test
    fun `deleteHabit successful`() {
        var habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)

        assertDoesNotThrow {
            habitService.deleteHabit(newHabitId)
        }

        habit = habitRepo.findByIdOrNull(newHabitId)
        assertNull(habit)
    }

    @Test
    fun `deleteHabit habit doesn't exist`() {
        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabit(256547)
        }
    }

    @Test
    fun `getHabitCompletions successful`() {
        val habit = Habit(
            name = "Habit name",
            category = "Entertainment",
            user = userRepo.findByIdOrNull(4) ?: throw  EntityNotFoundException(),
            type = 1,
            description = "habit description",
            difficulty = 1
        )
        val habitDb = habitRepo.save(habit)

        val habitCompletions = listOf(
            HabitCompletion(
                date = Instant.now(),
                habit = habitDb,
                rating = 3,
                isPositive = true
            ),
            HabitCompletion(
                date = Instant.now(),
                habit = habitDb,
                rating = 2,
                isPositive = false
            ),
        )
        habitCompletionRepo.saveAll(habitCompletions)

        val completionsDb = habitService.getHabitCompletions(habitDb.id!!)
        assertThat(completionsDb).isNotEmpty
        assertEquals(2, completionsDb.size)
        assertThat(completionsDb[0]).matches { it.rating == 3 && it.isPositive }
        assertThat(completionsDb[1]).matches { it.rating == 2 && !it.isPositive }

        // deleting
        habitRepo.deleteById(habitDb.id!!)
        assertThat(habitCompletionRepo.findByIdOrNull(completionsDb[0].id!!)).isNull()
    }

    @Test
    fun `getHabitCompletions habit not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            habitService.getHabitCompletions(646545)
        }
    }

    @Test
    fun `getHabitCompletions are empty`() {
        val habit = Habit(
            name = "Habit name",
            category = "Entertainment",
            user = userRepo.findByIdOrNull(4) ?: throw  EntityNotFoundException(),
            type = 1,
            description = "habit description",
            difficulty = 1
        )
        val habitDb = habitRepo.save(habit)


        val completionsDb = habitService.getHabitCompletions(habitDb.id!!)
        assertThat(completionsDb).isEmpty()

        // deleting
        habitRepo.deleteById(habitDb.id!!)
    }

    @Test
    fun `addHabitCompletion successful`() {
        val timeNow = Instant.now()

        var completion: HabitCompletionDto? = null

        assertDoesNotThrow {
            completion = habitService.addHabitCompletion(HabitCompletionReceiveDto(newHabitId, timeNow, 5))
        }
        assertNotNull(completion)
        completion?.let {
            assertEquals(timeNow, it.date)
            assertNotNull(it.id)
            assertThat(it.id!!).isGreaterThan(0)
        }
    }

    @Test
    fun `addHabitCompletion wrong type`() {
        val timeNow = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            habitService.addHabitCompletion(HabitCompletionReceiveDto(newHabitId, timeNow, 5, false))
        }
    }

    @Test
    fun `addHabitCompletion habit doesn't exist`() {
        val timeNow = Instant.now()

        var completion: HabitCompletionDto? = null

        assertThrows(EntityNotFoundException::class.java) {
            completion = habitService.addHabitCompletion(HabitCompletionReceiveDto(654654, timeNow, 5))
        }
        assertNull(completion)
    }

    @Test
    fun `deleteHabitCompletion successful`() {
        assertThat(newCompletionId).isGreaterThan(0)
        assertDoesNotThrow {
            habitService.deleteHabitCompletion(newCompletionId)
        }

        val completion = habitCompletionRepo.findByIdOrNull(newCompletionId)
        assertNull(completion)

        val habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)
    }

    @Test
    fun `deleteHabitCompletion completion doesn't exist`() {
        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabitCompletion(355601)
        }
    }
}