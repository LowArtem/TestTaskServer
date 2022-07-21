package com.trialbot.tasktest.repositories

import com.trialbot.tasktest.models.Task
import com.trialbot.tasktest.models.TaskUser
import com.trialbot.tasktest.models.TaskUserKey
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TaskUserRepository : CrudRepository<TaskUser, TaskUserKey> {

}

interface TaskRepository : CrudRepository<Task, Int> {

    @Query("select t from Task t inner join t.taskUsers users where users.id.userId = ?1")
    fun findByUsers_Id_UserId(userId: Int): List<Task>

    @Modifying
    @Query("update Task t set t.status = ?1 where t.id = ?2")
    fun updateTaskSetStatusForId(status: Boolean, id: Int): Int
}