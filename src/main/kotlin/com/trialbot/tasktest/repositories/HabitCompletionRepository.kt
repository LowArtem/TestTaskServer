package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.HabitCompletion
import org.springframework.data.repository.CrudRepository

interface HabitCompletionRepository : CrudRepository<HabitCompletion, Int> {
}