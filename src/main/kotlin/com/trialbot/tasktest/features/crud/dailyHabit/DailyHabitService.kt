package com.trialbot.tasktest.features.crud.dailyHabit

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.DailyHabitCompletionRepository
import com.trialbot.tasktest.repositories.DailyHabitRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.getUserFromToken
import com.trialbot.tasktest.utils.getUserIdFromToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class DailyHabitService(
    @Autowired private val dailyHabitRepo: DailyHabitRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val dailyHabitCompletionRepo: DailyHabitCompletionRepository
) {

    fun getDailyHabitsByUser(token: String): List<DailyHabitResponseDto> {
        val userId = token.getUserIdFromToken()
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        if (!userRepo.existsUserById(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val dailies = dailyHabitRepo.findAllByUser_Id(userId)
        return dailies.map { it.toResponseDto() }
    }

    fun addDailyHabit(token: String, habit: DailyHabitReceiveDto): DailyHabitResponseDto {
        val user: User = token.getUserFromToken(userRepo)
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val habitDb = DailyHabit(
            name = habit.name,
            category = habit.category,
            user = user,
            deadline = habit.deadline,
            status = false,
            description = habit.description,
            difficulty = habit.difficulty
        )

        return dailyHabitRepo.save(habitDb).toResponseDto()
    }

    fun updateDailyHabit(habit: DailyHabitResponseDto): DailyHabitResponseDto {
        val habitDb = dailyHabitRepo.findByIdOrNull(habit.id ?: -1)
            ?: throw EntityNotFoundException(DAILY_HABIT_NOT_FOUND_ERROR_MESSAGE)

        habitDb.name = habit.name
        habitDb.category = habit.category
        habitDb.deadline = habit.deadline
        habitDb.status = habit.status
        habitDb.description = habit.description
        habitDb.difficulty = habit.difficulty

        return dailyHabitRepo.save(habitDb).toResponseDto()
    }

    fun deleteDailyHabit(habitId: Int) {
        if (dailyHabitRepo.findByIdOrNull(habitId) == null)
            throw EntityNotFoundException(DAILY_HABIT_NOT_FOUND_ERROR_MESSAGE)

        dailyHabitRepo.deleteById(habitId)
    }

    @Transactional
    fun getDailyHabitCompletions(habitId: Int): List<DailyHabitCompletionDto> {
        val habit = dailyHabitRepo.findByIdOrNull(habitId)
            ?: throw EntityNotFoundException(DAILY_HABIT_NOT_FOUND_ERROR_MESSAGE)

        return habit.completions.map { it.toDto() }
    }

    fun addDailyHabitCompletion(requestData: DailyHabitCompletionReceiveDto): DailyHabitCompletionDto {
        val dailyHabit = dailyHabitRepo.findByIdOrNull(requestData.dailyHabitId) ?: throw EntityNotFoundException(
            DAILY_HABIT_NOT_FOUND_ERROR_MESSAGE
        )

        val dailyHabitCompletion = DailyHabitCompletion(requestData.date, dailyHabit, requestData.rating)
        return dailyHabitCompletionRepo.save(dailyHabitCompletion).toDto()
    }

    fun deleteDailyHabitCompletion(habitCompletionId: Int) {
        if (!dailyHabitRepo.existsById(habitCompletionId))
            throw EntityNotFoundException(DAILY_HABIT_COMPLETION_NOT_FOUND_ERROR_MESSAGE)

        dailyHabitCompletionRepo.deleteById(habitCompletionId)
    }



    companion object {
        private const val USER_NOT_FOUND_ERROR_MESSAGE = "User with this id doesn't exist"
        private const val DAILY_HABIT_NOT_FOUND_ERROR_MESSAGE = "Daily habit with this id doesn't exist"
        private const val DAILY_HABIT_COMPLETION_NOT_FOUND_ERROR_MESSAGE = "Daily habit completion with this id doesn't exist"
    }
}