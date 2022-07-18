package com.trialbot.tasktest.services

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.HabitCompletionRepository
import com.trialbot.tasktest.repositories.HabitRepository
import com.trialbot.tasktest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@Service
class HabitService(
    @Autowired private val habitRepo: HabitRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val habitCompletionRepo: HabitCompletionRepository
) {

    fun getHabitsByUser(userId: Int): List<HabitDto> {
        if (!userRepo.checkIfUserExists(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habits = habitRepo.findAllByUser_Id(userId)
        return habits.map { it.toDto() }
    }

    fun addHabit(userId: Int, habit: HabitDto): HabitDto {
        val user: User = userRepo.findByIdOrNull(userId)
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habitDb = Habit(
            name = habit.name,
            category = habit.category,
            user = user,
            type = habit.type,
            description = habit.description,
            difficulty = habit.difficulty
        )

        return habitRepo.save(habitDb).toDto()
    }

    fun updateHabit(userId: Int, habit: HabitDto): HabitDto {
        if (!userRepo.checkIfUserExists(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habitDb = habitRepo.findByIdOrNull(habit.id ?: -1)
            ?: throw EntityNotFoundException(HABIT_NOT_FOUND_ERROR_MESSAGE)

        habitDb.name = habit.name
        habitDb.description = habit.description
        habitDb.type = habit.type
        habitDb.category = habit.category
        habitDb.difficulty = habit.difficulty

        return habitRepo.save(habitDb).toDto()
    }

    fun deleteHabit(userId: Int, habitId: Int) {
        if (!userRepo.checkIfUserExists(userId)) throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)
        habitRepo.deleteById(habitId)
    }

    fun addHabitCompletion(
        userId: Int, habitId: Int, date: LocalDateTime, rating: Int = 5, isPositive: Boolean = true
    ): HabitCompletionDto {
        if (!userRepo.checkIfUserExists(userId)) throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)
        val habit = habitRepo.findByIdOrNull(habitId) ?: throw EntityNotFoundException(HABIT_NOT_FOUND_ERROR_MESSAGE)

        val habitCompletion = HabitCompletion(date, habit, rating, isPositive)
        return habitCompletionRepo.save(habitCompletion).toDto()
    }

    fun deleteHabitCompletion(userId: Int, habitCompletionId: Int) {
        if (!userRepo.checkIfUserExists(userId)) throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)
        habitCompletionRepo.deleteById(habitCompletionId)
    }

    companion object {
        private const val USER_NOT_FOUND_ERROR_MESSAGE = "User with this id doesn't exist"
        private const val HABIT_NOT_FOUND_ERROR_MESSAGE = "Habit with this id doesn't exist"
    }
}