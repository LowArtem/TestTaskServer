package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.repositories.TaskRepository
import com.trialbot.tasktest.repositories.TaskUserRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.CurrentDateTimeProvider
import com.trialbot.tasktest.utils.getUserFromToken
import com.trialbot.tasktest.utils.getUserIdFromToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class TaskService(
    @Autowired private val taskRepo: TaskRepository,
    @Autowired private val taskUserRepository: TaskUserRepository,
    @Autowired private val userRepo: UserRepository,
) {

    fun getTasksByUser(token: String): List<TaskResponseDto> {
        val userId = token.getUserIdFromToken()
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        if (!userRepo.existsUserById(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val tasks = taskRepo.findByUsers_Id_UserId(userId)
        return tasks.map { it.toResponseDto() }
    }

    fun addTask(token: String, taskReceive: TaskReceiveDto): TaskResponseDto {
        val user: User = token.getUserFromToken(userRepo)
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val task = Task(
            name = taskReceive.name,
            deadline = taskReceive.deadline,
            status = false,
            difficulty = taskReceive.difficulty,
            priority = taskReceive.priority,
            description = taskReceive.description,
        )

        val taskSaved = taskRepo.save(task)
        val taskToUser = TaskUser(
            user = user,
            task = taskSaved,
            date = null
        )

        taskUserRepository.save(taskToUser)
        return taskRepo.findByIdOrNull(taskSaved.id!!)!!.toResponseDto()
    }

    fun updateTask(task: TaskResponseDto): TaskResponseDto {
        val taskDb = taskRepo.findByIdOrNull(task.id ?: -1)
            ?: throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        taskDb.name = task.name
        taskDb.deadline = task.deadline
        taskDb.status = task.status
        taskDb.priority = task.priority
        taskDb.difficulty = task.difficulty
        taskDb.description = task.description

        return taskRepo.save(taskDb).toResponseDto()
    }

    @Transactional
    fun updateTaskStatus(taskId: Int, status: Boolean) {
        if (!taskRepo.existsById(taskId))
            throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        val successResult = taskRepo.updateTaskSetStatusForId(status, taskId) == 1

        if (!successResult)
            throw UnsupportedOperationException("Cannot update this entity")
    }

    fun deleteTask(taskId: Int) {
        if (!taskRepo.existsById(taskId))
            throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        taskRepo.deleteById(taskId)
    }



    companion object {
        private const val USER_NOT_FOUND_ERROR_MESSAGE = "User with this id doesn't exist"
        private const val TASK_NOT_FOUND_ERROR_MESSAGE = "Task with this id doesn't exist"
    }
}