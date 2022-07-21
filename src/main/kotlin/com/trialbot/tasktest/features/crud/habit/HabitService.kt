package com.trialbot.tasktest.features.crud.habit

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.HabitCompletionRepository
import com.trialbot.tasktest.repositories.HabitRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.getUserFromToken
import com.trialbot.tasktest.utils.getUserIdFromToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class HabitService(
    @Autowired private val habitRepo: HabitRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val habitCompletionRepo: HabitCompletionRepository,
) {

    fun getHabitsByUser(token: String): List<HabitResponseDto> {
        val userId = token.getUserIdFromToken()
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        if (!userRepo.existsUserById(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habits = habitRepo.findAllByUser_Id(userId)
        return habits.map { it.toResponseDto() }
    }

    fun addHabit(token: String, habit: HabitReceiveDto): HabitResponseDto {
        val user: User = token.getUserFromToken(userRepo)
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habitDb = Habit(
            name = habit.name,
            category = habit.category,
            user = user,
            type = habit.type,
            description = habit.description,
            difficulty = habit.difficulty
        )

        return habitRepo.save(habitDb).toResponseDto()
    }

    fun updateHabit(habit: HabitResponseDto): HabitResponseDto {
        val habitDb = habitRepo.findByIdOrNull(habit.id ?: -1)
            ?: throw EntityNotFoundException(HABIT_NOT_FOUND_ERROR_MESSAGE)

        habitDb.name = habit.name
        habitDb.description = habit.description
        habitDb.type = habit.type
        habitDb.category = habit.category
        habitDb.difficulty = habit.difficulty

        return habitRepo.save(habitDb).toResponseDto()
    }

    fun deleteHabit(habitId: Int) {
        if (habitRepo.findByIdOrNull(habitId) == null) throw EntityNotFoundException(HABIT_NOT_FOUND_ERROR_MESSAGE)
        habitRepo.deleteById(habitId)
    }

    @Transactional
    fun getHabitCompletions(habitId: Int): List<HabitCompletionDto> {
        val habit = habitRepo.findByIdOrNull(habitId)
            ?: throw EntityNotFoundException(HABIT_NOT_FOUND_ERROR_MESSAGE)

        return habit.completions.map { it.toDto() }
    }

    fun addHabitCompletion(requestData: HabitCompletionReceiveDto): HabitCompletionDto {
        val habit = habitRepo.findByIdOrNull(requestData.habitId) ?: throw EntityNotFoundException(
            HABIT_NOT_FOUND_ERROR_MESSAGE
        )

        val habitCompletion = HabitCompletion(requestData.date, habit, requestData.rating, requestData.isPositive)
        return habitCompletionRepo.save(habitCompletion).toDto()
    }

    fun deleteHabitCompletion(habitCompletionId: Int) {
        if (!habitCompletionRepo.existsById(habitCompletionId))
            throw EntityNotFoundException(HABIT_COMPLETION_NOT_FOUND_ERROR_MESSAGE)

        habitCompletionRepo.deleteById(habitCompletionId)
    }

    companion object {
        private const val USER_NOT_FOUND_ERROR_MESSAGE = "User with this id doesn't exist"
        private const val HABIT_NOT_FOUND_ERROR_MESSAGE = "Habit with this id doesn't exist"
        private const val HABIT_COMPLETION_NOT_FOUND_ERROR_MESSAGE = "Habit completion with this id doesn't exist"
    }
}