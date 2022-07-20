package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.DailyHabitCompletion
import org.springframework.data.repository.CrudRepository

interface DailyHabitCompletionRepository : CrudRepository<DailyHabitCompletion, Int> {
}