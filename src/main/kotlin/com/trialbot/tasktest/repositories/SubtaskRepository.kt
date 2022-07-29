package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.Subtask
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SubtaskRepository : CrudRepository<Subtask, Int> {

    @Modifying
    @Query("update Subtask t set t.status = ?1 where t.id = ?2")
    fun updateSubtaskSetStatusForId(status: Boolean, id: Int): Int
}