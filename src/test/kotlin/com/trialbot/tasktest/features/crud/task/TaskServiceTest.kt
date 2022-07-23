package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.Task
import com.trialbot.tasktest.models.TaskReceiveDto
import com.trialbot.tasktest.models.TaskResponseDto
import com.trialbot.tasktest.models.toResponseDto
import com.trialbot.tasktest.repositories.TaskRepository
import com.trialbot.tasktest.repositories.UserRepository
import com.trialbot.tasktest.utils.getUserIdFromToken
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import javax.persistence.EntityNotFoundException

@SpringBootTest
internal class TaskServiceTest(
    @Autowired private val taskRepo: TaskRepository,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val taskService: TaskService
) {

    @Test
    fun `getTasksByUser successful`() {
        var tasks: List<TaskResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "7"
        every {
            tokenMock.getUserIdFromToken()
        } returns 7

        assertDoesNotThrow {
            tasks = taskService.getTasksByUser(tokenMock)
        }
        assertThat(tasks).isNotEmpty
    }

    @Test
    fun `getTasksByUser user not found`() {
        var tasks: List<TaskResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = ""
        every {
            tokenMock.getUserIdFromToken()
        } returns 787866

        assertThrows(EntityNotFoundException::class.java) {
            tasks = taskService.getTasksByUser(tokenMock)
        }
        assertThat(tasks).isEmpty()
    }

    @Test
    fun `getTasksByUser tasks are empty`() {
        var tasks: List<TaskResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "2"
        every {
            tokenMock.getUserIdFromToken()
        } returns 2

        assertDoesNotThrow {
            tasks = taskService.getTasksByUser(tokenMock)
        }
        assertThat(tasks).isEmpty()
    }

    @Test
    fun `addTask successful`() {
        val timeNow = Instant.now()

        val taskToAdd = TaskReceiveDto(
            name = "Brand New Task",
            deadline = timeNow,
            difficulty = 2,
            priority = 1,
            description = "Brand new task description"
        )

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "4"
        every {
            tokenMock.getUserIdFromToken()
        } returns 4

        var taskAdded: TaskResponseDto? = null
        assertDoesNotThrow {
            taskAdded = taskService.addTask(tokenMock, taskToAdd)
        }
        assertNotNull(taskAdded)
        assertNotNull(taskAdded!!.id)
        assertThat(taskAdded!!.id).isGreaterThan(0)

        assertEquals(taskToAdd.name, taskAdded!!.name)
        assertEquals(taskToAdd.description, taskAdded!!.description)
        assertEquals(taskToAdd.deadline, timeNow)

        val tasksByThisUser: List<Task> = taskRepo.findByUsers_Id_UserId(4)
        val currentTaskDb = taskRepo.findByIdOrNull(taskAdded!!.id!!) ?: throw EntityNotFoundException()

        assertThat(tasksByThisUser).contains(currentTaskDb)

        // delete created object
        taskRepo.deleteById(taskAdded!!.id!!)
        assertNull(taskRepo.findByIdOrNull(taskAdded!!.id!!))
    }

    @Test
    fun `addTask user not found`() {
        val timeNow = Instant.now()

        val taskToAdd = TaskReceiveDto(
            name = "Brand New Task",
            deadline = timeNow,
            difficulty = 2,
            priority = 1,
            description = "Brand new task description"
        )

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = ""
        every {
            tokenMock.getUserIdFromToken()
        } returns 4654657

        var taskAdded: TaskResponseDto? = null
        assertThrows(EntityNotFoundException::class.java) {
            taskAdded = taskService.addTask(tokenMock, taskToAdd)
        }
        assertNull(taskAdded)
    }

    @Test
    fun `updateTask successful`() {
        val taskDb = taskRepo.findByIdOrNull(30)?.toResponseDto()
            ?: throw EntityNotFoundException()

        taskDb.name = "Some name, that make sense"
        taskDb.description = "English words are way better that strange string of letters"
        taskDb.status = !taskDb.status

        val taskUpdated = taskService.updateTask(taskDb)
        assertEquals(taskDb, taskUpdated)
    }

    @Test
    fun `updateTask task not found`() {
        val task = TaskResponseDto(
            name = "sdfsdf",
            deadline = Instant.now(),
            difficulty = 2,
            priority = 2,
            id = 654655,
            description = "ajshldf"
        )

        assertThrows(EntityNotFoundException::class.java) {
            taskService.updateTask(task)
        }
    }

    @Test
    fun `updateTaskStatus successful`() {
        val taskId = 30

        val taskDb = taskRepo.findByIdOrNull(taskId)?.toResponseDto()
            ?: throw EntityNotFoundException()

        taskDb.status = !taskDb.status

        assertDoesNotThrow {
            taskService.updateTaskStatus(taskId, taskDb.status)
        }

        val taskDbAfter = taskRepo.findByIdOrNull(taskId)?.toResponseDto()
            ?: throw EntityNotFoundException()

        assertEquals(taskDb, taskDbAfter)
    }

    @Test
    fun `updateTaskStatus task not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            taskService.updateTaskStatus(684684, true)
        }
    }

    @Test
    fun `deleteTask successful`() {
        val task = Task(
            name = "Name",
            deadline = Instant.now(),
            status = false,
            difficulty = 2,
            priority = 2,
            description = "sasdfdfd",
            taskUsers = setOf(),
        )
        val savedId = taskRepo.save(task).id ?: throw EntityNotFoundException()

        assertDoesNotThrow {
            taskService.deleteTask(savedId)
        }
        assertNull(taskRepo.findByIdOrNull(savedId))
    }

    @Test
    fun `deleteTask task not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            taskService.deleteTask(684684)
        }
    }
}