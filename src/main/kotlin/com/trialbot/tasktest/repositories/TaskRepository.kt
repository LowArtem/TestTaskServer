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
    fun findByUser(userId: Int): List<Task>

    @Query("select t from Task t inner join t.taskUsers users where users.id.userId = ?1 and t.status = ?2 order by users.date desc")
    fun findByUserAndStatusSortedByDateDesc(userId: Int, status: Boolean): List<Task>

    @Modifying(clearAutomatically = true)
    @Query("update tasks set status = ?1 where id = ?2", nativeQuery = true)
    fun updateTaskSetStatusForId(status: Boolean, id: Int): Int

    @Query("select * from tasks where parentrepeatingtaskid = ?1", nativeQuery = true)
    fun findChildRepeatingTasks(parentTaskId: Int): List<Task>
}