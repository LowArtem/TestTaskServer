package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.DailyHabit
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DailyHabitRepository : CrudRepository<DailyHabit, Int> {

    fun findAllByUser_Id(id: Int): List<DailyHabit>

    @Modifying
    @Query("update DailyHabit d set d.status = ?1 where d.id = ?2")
    fun updateDailyHabitSetStatusForId(status: Boolean, id: Int): Boolean
}