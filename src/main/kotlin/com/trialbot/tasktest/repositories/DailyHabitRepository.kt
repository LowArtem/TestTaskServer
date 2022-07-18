package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.DailyHabit
import org.springframework.data.repository.CrudRepository

interface DailyHabitRepository : CrudRepository<DailyHabit, Int> {
}