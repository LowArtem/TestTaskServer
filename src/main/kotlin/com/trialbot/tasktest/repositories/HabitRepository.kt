package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.Habit
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Suppress("FunctionName")
@Repository
interface HabitRepository : CrudRepository<Habit, Int> {

    fun findAllByUser_Id(id: Int): List<Habit>
}