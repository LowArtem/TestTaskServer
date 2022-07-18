package com.trialbot.tasktest.services

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.HabitCompletionRepository
import com.trialbot.tasktest.repositories.HabitRepository
import com.trialbot.tasktest.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import org.hamcrest.Matchers.*
import org.hamcrest.MatcherAssert.assertThat


@SpringBootTest
internal class HabitServiceTest(
    @Autowired private val habitRepo: HabitRepository,
    @Autowired private val habitCompletionRepo: HabitCompletionRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val habitService: HabitService
) {

    private var newHabitId: Int = -1
    private var newCompletionId: Int = -1

    private lateinit var user: User
    private lateinit var newHabit: Habit

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
            type = 1,
            description = "test habit for testing",
            difficulty = 2
        )
        val savedHabit = habitRepo.save(habitDb)
        newHabitId = savedHabit.id!!

        val completion = HabitCompletion(LocalDateTime.now(), savedHabit)
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
    fun `getHabitsByUser successful`() {
        val userId = 7
        var habits: List<HabitDto> = listOf()

        assertDoesNotThrow {
            habits = habitService.getHabitsByUser(userId)
        }
        assertEquals(23, habits.size)

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
        val userId = 2
        var habits: List<HabitDto> = listOf()

        assertDoesNotThrow {
            val habitTest = habitService.getHabitsByUser(userId)
            habits = habitTest
        }
        assertNotNull(habits)
        assertEquals(0, habits.size)
    }

    @Test
    fun `getHabitsByUser user doesn't exist`() {
        val userId = 2567887

        assertThrows(EntityNotFoundException::class.java) {
            habitService.getHabitsByUser(userId)
        }
    }

    @Test
    fun `addHabit successful`() {
        val userId = 7
        var habitAdded: HabitDto? = null

        assertDoesNotThrow {
            habitAdded = habitService.addHabit(userId, newHabit.toDto())
        }
        assertNotNull(habitAdded)
        habitAdded?.let { habit ->
            assertNotNull(habit.id)
            assertThat(habit.id!!, greaterThan(0))
            assertEquals(newHabit.name, habit.name)
            assertEquals(newHabit.description, habit.description)
            assertEquals(newHabit.category, habit.category)

            habitRepo.deleteById(habit.id!!)
        }
    }

    @Test
    fun `addHabit user doesn't exist`() {
        val userId = 2567887

        assertThrows(EntityNotFoundException::class.java) {
            habitService.addHabit(userId, newHabit.toDto())
        }
    }

    @Test
    fun `updateHabit successful`() {
        val userId = 7
        val habit = habitRepo.findByIdOrNull(newHabitId) ?: throw EntityNotFoundException()
        assertNotNull(habit)

        val newHabitDto = habit.toDto()
        newHabitDto.description = "ja;slkdfaj;sga;jfglfglaa[dpof"

        var habitUpdated: HabitDto? = null
        assertDoesNotThrow {
            habitUpdated = habitService.updateHabit(userId, newHabitDto)
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
        val userId = 7
        val habit = habitRepo.findByIdOrNull(newHabitId) ?: throw EntityNotFoundException()
        assertNotNull(habit)

        val newHabitDto = habit.toDto()
        newHabitDto.description = "ja;slkdfaj;sga;jfglfglaa[dpof"
        val habitDto = newHabitDto.copy(id = 827364)

        var habitUpdated: HabitDto? = null
        assertThrows(EntityNotFoundException::class.java) {
            habitUpdated = habitService.updateHabit(userId, habitDto)
        }
        assertNull(habitUpdated)
    }

    @Test
    fun `updateHabit user doesn't exist`() {
        val userId = 2567887

        val habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)

        assertThrows(EntityNotFoundException::class.java) {
            habitService.updateHabit(userId, habit!!.toDto())
        }
    }

    @Test
    fun `deleteHabit successful`() {
        val userId = 7

        var habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)

        assertDoesNotThrow {
            habitService.deleteHabit(userId, newHabitId)
        }

        habit = habitRepo.findByIdOrNull(newHabitId)
        assertNull(habit)
    }

    @Test
    fun `deleteHabit habit doesn't exist`() {
        val userId = 7

        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabit(userId, 256547)
        }
    }

    @Test
    fun `deleteHabit user doesn't exist`() {
        val userId = 2567887

        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabit(userId, newHabitId)
        }
    }

    @Test
    fun `addHabitCompletion successful`() {
        val userId = 7

        val timeNow = LocalDateTime.now()

        var completion: HabitCompletionDto? = null

        assertDoesNotThrow {
            completion = habitService.addHabitCompletion(userId, newHabitId, timeNow)
        }
        assertNotNull(completion)
        completion?.let {
            assertEquals(timeNow, it.date)
            assertNotNull(it.id)
            assertThat(it.id!!, greaterThan(0))
        }
    }

    @Test
    fun `addHabitCompletion habit doesn't exist`() {
        val userId = 7

        val timeNow = LocalDateTime.now()

        var completion: HabitCompletionDto? = null

        assertThrows(EntityNotFoundException::class.java) {
            completion = habitService.addHabitCompletion(userId, 654167, timeNow)
        }
        assertNull(completion)
    }

    @Test
    fun `addHabitCompletion user doesn't exist`() {
        val userId = 2567887

        assertThrows(EntityNotFoundException::class.java) {
            habitService.addHabitCompletion(userId, newHabitId, LocalDateTime.now())
        }
    }

    @Test
    fun `deleteHabitCompletion successful`() {
        val userId = 7
        assertThat(newCompletionId, greaterThan(0))
        assertDoesNotThrow {
            habitService.deleteHabitCompletion(userId, newCompletionId)
        }

        val completion = habitCompletionRepo.findByIdOrNull(newCompletionId)
        assertNull(completion)

        val habit = habitRepo.findByIdOrNull(newHabitId)
        assertNotNull(habit)
    }

    @Test
    fun `deleteHabitCompletion completion doesn't exist`() {
        val userId = 7

        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabitCompletion(userId, 355601)
        }
    }

    @Test
    fun `deleteHabitCompletion user doesn't exist`() {
        val userId = 2567887

        assertThrows(EntityNotFoundException::class.java) {
            habitService.deleteHabitCompletion(userId, newCompletionId)
        }
    }
}