package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.models.enums.RepeatingInterval
import com.trialbot.tasktest.models.enums.addToDate
import com.trialbot.tasktest.repositories.SubtaskRepository
import com.trialbot.tasktest.repositories.TaskRepository
import com.trialbot.tasktest.repositories.TaskUserRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.getUserFromToken
import com.trialbot.tasktest.utils.getUserIdFromToken
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class TaskService(
    private val taskRepo: TaskRepository,
    private val taskUserRepository: TaskUserRepository,
    private val userRepo: UserRepository,
    private val subtaskRepo: SubtaskRepository,
) {

    fun getTasksByUser(token: String): List<TaskShortResponseDto> {
        val userId = token.getUserIdFromToken()
            ?: throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        if (!userRepo.existsUserById(userId))
            throw EntityNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE)

        val tasks = taskRepo.findByUsers_Id_UserId(userId)
        return tasks.map { it.toShortResponseDto() }
    }

    @Transactional
    fun getTask(taskId: Int): TaskResponseDto {
        val task = taskRepo.findByIdOrNull(taskId)
            ?: throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        return task.toResponseDto()
    }

    @Transactional
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
            repeatingInterval = taskReceive.repeatingInterval,
            notification = taskReceive.notification
        )

        val taskSaved = taskRepo.save(task)
        val taskToUser = TaskUser(
            user = user,
            task = taskSaved,
            date = null
        )

        taskUserRepository.save(taskToUser)
        val taskReturnable = taskRepo.findByIdOrNull(taskSaved.id!!)!!.toResponseDto()
        taskReturnable.subtasks = addSubtasks(taskReceive.subtasks, taskSaved)

        return taskReturnable
    }

    private fun addSubtasks(subtasks: Set<SubtaskReceiveDto>?, parentTask: Task): Set<SubtaskResponseDto> {
        if (subtasks == null) return setOf()

        val subtasksDb: List<Subtask> = subtasks.map { subtask ->
            return@map Subtask(
                text = subtask.text,
                status = subtask.status,
                parentTask = parentTask
            )
        }

        return subtaskRepo.saveAll(subtasksDb).map { it.toResponseDto() }.toSet()
    }

    @Transactional
    fun addTaskRepeat(repeatableTaskId: Int): TaskResponseDto {
        val task = taskRepo.findByIdOrNull(repeatableTaskId)
            ?: throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        if (task.repeatingInterval == 0) throw IllegalStateException("Task should be repeatable")
        if (task.deadline == null) throw IllegalStateException("Task's deadline cannot be null")

        val newTask = task.clone()
        newTask.id = null

        newTask.deadline = RepeatingInterval.values()[task.repeatingInterval].addToDate(task.deadline!!)

        if (task.parentRepeatingTask == null) {
            newTask.parentRepeatingTask = task.id
        }

        // Make subtasks' copies
        if (task.subtasks.isNotEmpty()) {
            val newSubtasks: MutableSet<Subtask> = mutableSetOf()
            for (sub in task.subtasks) {
                val newSub = sub.clone()
                newSub.id = null
                newSubtasks.add(newSub)
            }
            newTask.subtasks = newSubtasks
        } else {
            newTask.subtasks = setOf()
        }

        // Make connections with users
        if (task.taskUsers.isNotEmpty()) {
            val newTaskUsers: MutableSet<TaskUser> = mutableSetOf()
            for (tu in task.taskUsers) {
                val newTaskUser = TaskUser(tu.user, null, tu.task)
                newTaskUsers.add(newTaskUser)
            }
            newTask.taskUsers = newTaskUsers
        } else {
            newTask.taskUsers = setOf()
        }


        return taskRepo.save(newTask).toResponseDto()
    }

    @Transactional
    fun updateTask(taskReceive: TaskUpdateReceiveDto): TaskResponseDto {
        val taskDb = taskRepo.findByIdOrNull(taskReceive.id ?: -1)
            ?: throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        taskDb.name = taskReceive.name
        taskDb.deadline = taskReceive.deadline
        taskDb.status = taskReceive.status
        taskDb.priority = taskReceive.priority
        taskDb.difficulty = taskReceive.difficulty
        taskDb.description = taskReceive.description
        taskDb.repeatingInterval = taskReceive.repeatingInterval
        taskDb.notification = taskReceive.notification

        val task = taskRepo.save(taskDb).toResponseDto()

        if (taskReceive.subtasks != taskDb.subtasks.map { it.toResponseDto() }) {
            task.subtasks = updateSubtasks(taskReceive.subtasks, taskDb)
        }

        return task
    }

    fun updateSubtasks(subtasks: Set<SubtaskUpdateReceiveDto>, parentTask: Task): Set<SubtaskResponseDto> {
        return subtasks.map {subtask ->
            val subtaskDb = subtaskRepo.findByIdOrNull(subtask.id ?: -1)
            if (subtaskDb != null) {
                subtaskDb.text = subtask.text
                subtaskDb.status = subtask.status

                return@map subtaskRepo.save(subtaskDb).toResponseDto()
            } else {
                val newSubtask = Subtask(
                    text = subtask.text,
                    status = subtask.status,
                    parentTask = parentTask
                )

                return@map subtaskRepo.save(newSubtask).toResponseDto()
            }

        }.toSet()
    }

    @Transactional
    fun updateTaskStatus(taskId: Int, status: Boolean) {
        if (!taskRepo.existsById(taskId))
            throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        val successResult = taskRepo.updateTaskSetStatusForId(status, taskId) == 1
        if (!successResult)
            throw UnsupportedOperationException("Cannot update this entity")
    }

    @Transactional
    fun updateSubtaskStatus(subtaskId: Int, status: Boolean) {
        if (!subtaskRepo.existsById(subtaskId))
            throw EntityNotFoundException(SUBTASK_NOT_FOUND_ERROR_MESSAGE)

        val successResult = subtaskRepo.updateSubtaskSetStatusForId(status, subtaskId) == 1
        if (!successResult)
            throw UnsupportedOperationException("Cannot update this entity")
    }

    @Transactional
    fun deleteTask(taskId: Int) {
        if (!taskRepo.existsById(taskId))
            throw EntityNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)

        // deleting child repeating tasks
        val task = taskRepo.findByIdOrNull(taskId)!!
        if (task.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(task.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }

        taskRepo.deleteById(taskId)
    }

    fun deleteSubtask(subtaskId: Int) {
        if (!subtaskRepo.existsById(subtaskId))
            throw EntityNotFoundException(SUBTASK_NOT_FOUND_ERROR_MESSAGE)

        subtaskRepo.deleteById(subtaskId)
    }



    companion object {
        private const val USER_NOT_FOUND_ERROR_MESSAGE = "User with this id doesn't exist"
        private const val TASK_NOT_FOUND_ERROR_MESSAGE = "Task with this id doesn't exist"
        private const val SUBTASK_NOT_FOUND_ERROR_MESSAGE = "Subtask with this id doesn't exist"
    }
}