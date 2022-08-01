package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.*
import com.trialbot.tasktest.models.enums.RepeatingInterval
import com.trialbot.tasktest.repositories.SubtaskRepository
import com.trialbot.tasktest.repositories.TaskRepository
import com.trialbot.tasktest.utils.getUserIdFromToken
import com.trialbot.tasktest.utils.toLocalDateTimeUTC
import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@SpringBootTest
internal class TaskServiceTest(
    @Autowired private val taskRepo: TaskRepository,
    @Autowired private val taskService: TaskService,
    @Autowired private val subtaskRepo: SubtaskRepository
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
    @Transactional
    fun `getTask with subtasks successful`() {
        var tasks: List<TaskResponseDto> = listOf()

        mockkStatic(String::getUserIdFromToken)

        val tokenMock = "1821" // user with subtasks
        every {
            tokenMock.getUserIdFromToken()
        } returns 1821

        assertDoesNotThrow {
            tasks = taskService.getTasksByUser(tokenMock)
        }
        assertThat(tasks).isNotEmpty
        assertTrue(tasks.any { it.subtasks.isNotEmpty() })
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
    fun `addTask task with subtasks successful`() {
        val timeNow = Instant.now()

        val taskToAdd = TaskReceiveDto(
            name = "Brand New Task",
            deadline = timeNow,
            difficulty = 2,
            priority = 1,
            description = "Brand new task description",
            subtasks = setOf(
                SubtaskReceiveDto("Subtask 1", false),
                SubtaskReceiveDto("Subtask 2", false),
                SubtaskReceiveDto("Subtask 3", false),
                SubtaskReceiveDto("Subtask 4", false),
                SubtaskReceiveDto("Subtask 5", false),
            )
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

        assertThat(taskAdded!!.subtasks).isNotEmpty
        assertThat(taskAdded!!.subtasks).hasSize(5)
        assertThat(taskAdded!!.subtasks.any { it.text == "Subtask 1" }).isTrue

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
    @Transactional
    fun `updateTask with no subtasks successful`() {
        val taskDb = taskRepo.findByIdOrNull(30)?.toUpdateReceiveDto()
            ?: throw EntityNotFoundException()

        taskDb.name = "Some name, that make sense"
        taskDb.description = "English words are way better than strange string of letters"
        taskDb.status = !taskDb.status

        val taskUpdated = taskService.updateTask(taskDb)
        assertEquals(taskDb.name, taskUpdated.name)
        assertEquals(taskDb.description, taskUpdated.description)
        assertEquals(taskDb.status, taskUpdated.status)
    }

    @Test
    @Transactional
    fun `updateTask with subtasks successful`() {
        val taskDb = taskRepo.findByIdOrNull(4)?.toUpdateReceiveDto()
            ?: throw EntityNotFoundException()

        taskDb.name = "Some name, that doesn't make sense"
        taskDb.description = "English words are way worse than strange string of letters"
        taskDb.status = !taskDb.status
        taskDb.subtasks = taskDb.subtasks + setOf(SubtaskUpdateReceiveDto("New text task", false))

        val taskUpdated = taskService.updateTask(taskDb)

        assertEquals(taskDb.name, taskUpdated.name)
        assertEquals(taskDb.description, taskUpdated.description)
        assertEquals(taskDb.status, taskUpdated.status)
        assertEquals(5, taskUpdated.subtasks.size)
        assertTrue(taskUpdated.subtasks.any { it.text == "New text task" })
    }

    @Test
    fun `updateTask task not found`() {
        val task = TaskUpdateReceiveDto(
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
    @Transactional
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
    fun `updateSubtaskStatus successful`() {
        val subtaskId = 99

        val taskDb = subtaskRepo.findByIdOrNull(subtaskId)?.toUpdateReceiveDto()
            ?: throw EntityNotFoundException()

        taskDb.status = !taskDb.status

        assertDoesNotThrow {
            taskService.updateSubtaskStatus(subtaskId, taskDb.status)
        }

        val taskDbAfter = subtaskRepo.findByIdOrNull(subtaskId)?.toUpdateReceiveDto()
            ?: throw EntityNotFoundException()

        assertEquals(taskDb, taskDbAfter)
    }

    @Test
    fun `updateSubtaskStatus subtask not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            taskService.updateSubtaskStatus(684684, true)
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

    @Test
    fun `deleteTask task that is a repeatable parent task`() {
        val task = Task(
            name = "New test mega name",
            deadline = Instant.now(),
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.WEEKLY.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )
        val taskSaved = taskRepo.save(task)
        val repeatedTask = taskService.addTaskRepeat(taskSaved.id!!)

        taskService.deleteTask(taskSaved.id!!)
        assertNull(taskRepo.findByIdOrNull(taskSaved.id!!))
        assertNull(taskRepo.findByIdOrNull(repeatedTask.id!!))
    }

    @Test
    fun `deleteTask task that is a repeatable child`() {
        val task = Task(
            name = "New test mega name",
            deadline = Instant.now(),
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.WEEKLY.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )
        val taskSaved = taskRepo.save(task)
        val repeatedTask = taskService.addTaskRepeat(taskSaved.id!!)

        val metaRepeatedTask = taskService.addTaskRepeat(repeatedTask.id!!)

        taskService.deleteTask(repeatedTask.id!!)
        assertNull(taskRepo.findByIdOrNull(repeatedTask.id!!))
        assertNotNull(taskRepo.findByIdOrNull(taskSaved.id!!))
        assertNotNull(taskRepo.findByIdOrNull(metaRepeatedTask.id!!))

        // delete testing objects
        if (taskSaved.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(taskSaved.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }
        taskRepo.deleteById(taskSaved.id!!)
    }

    @Test
    fun `deleteSubtask successful`() {
        val subtask = Subtask(
            text = "Brand new subtask",
            status = false,
            parentTask = taskRepo.findByIdOrNull(4) ?: throw EntityNotFoundException()
        )

        val savedId = subtaskRepo.save(subtask).id ?: throw EntityNotFoundException()

        assertDoesNotThrow {
            taskService.deleteSubtask(savedId)
        }
        assertNull(subtaskRepo.findByIdOrNull(savedId))
    }

    @Test
    fun `deleteSubtask subtask not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            taskService.deleteSubtask(684684)
        }
    }

    @Test
    fun `addTaskRepeat successful weekly`() {
        val task = Task(
            name = "New test mega name",
            deadline = Instant.now(),
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.WEEKLY.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )

        val taskSaved = taskRepo.save(task)

        val repeatedTask = taskService.addTaskRepeat(taskSaved.id!!)

        assertNotEquals(taskSaved.id, repeatedTask.id)
        assertEquals(taskSaved.name, repeatedTask.name)
        assertEquals(taskSaved.priority, repeatedTask.priority)
        assertEquals(taskSaved.description, repeatedTask.description)

        val time = repeatedTask.deadline!!.toLocalDateTimeUTC()
        val timeExpected = task.deadline!!.toLocalDateTimeUTC().plusDays(7)

        assertEquals(timeExpected.hour, time.hour)
        assertEquals(timeExpected.minute, time.minute)
        assertEquals(timeExpected.year, time.year)
        assertEquals(timeExpected.month, time.month)
        assertEquals(timeExpected.dayOfMonth, time.dayOfMonth)

        // delete testing objects
        if (taskSaved.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(taskSaved.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }
        taskRepo.deleteById(taskSaved.id!!)
    }

    @Test
    fun `addTaskRepeat successful monthly`() {
        val task = Task(
            name = "New test mega name",
            deadline = Instant.now(),
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.MONTHLY.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )

        val taskSaved = taskRepo.save(task)

        val repeatedTask = taskService.addTaskRepeat(taskSaved.id!!)

        assertNotEquals(taskSaved.id, repeatedTask.id)
        assertEquals(taskSaved.name, repeatedTask.name)
        assertEquals(taskSaved.priority, repeatedTask.priority)
        assertEquals(taskSaved.description, repeatedTask.description)

        val time = repeatedTask.deadline!!.toLocalDateTimeUTC()
        val timeExpected = task.deadline!!.toLocalDateTimeUTC().plusMonths(1)

        assertEquals(timeExpected.hour, time.hour)
        assertEquals(timeExpected.minute, time.minute)
        assertEquals(timeExpected.year, time.year)
        assertEquals(timeExpected.month, time.month)
        assertEquals(timeExpected.dayOfMonth, time.dayOfMonth)

        // delete testing objects
        if (taskSaved.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(taskSaved.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }
        taskRepo.deleteById(taskSaved.id!!)
    }

    @Test
    fun `addTaskRepeat task is non-repeatable`() {
        val task = Task(
            name = "New test mega name",
            deadline = Instant.now(),
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.NONE.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )

        val taskSaved = taskRepo.save(task)

        assertThrows(IllegalStateException::class.java) {
            val repeatedTask = taskService.addTaskRepeat(taskSaved.id!!)
        }

        // delete testing objects
        if (taskSaved.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(taskSaved.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }
        taskRepo.deleteById(taskSaved.id!!)
    }

    @Test
    fun `addTaskRepeat task not found`() {
        assertThrows(EntityNotFoundException::class.java) {
            taskService.addTaskRepeat(654654555)
        }
    }

    @Test
    fun `addTaskRepeat deadline is null`() {
        val task = Task(
            name = "New test mega name",
            deadline = null,
            status = false,
            difficulty = 1,
            priority = 2,
            repeatingInterval = RepeatingInterval.YEARLY.ordinal,
            parentRepeatingTask = null,
            description = "New test mega name description"
        )

        val taskSaved = taskRepo.save(task)

        assertThrows(IllegalStateException::class.java) {
            taskService.addTaskRepeat(taskSaved.id!!)
        }

        // delete testing objects
        if (taskSaved.repeatingInterval > 0) {
            val childRepeatingTasks = taskRepo.findChildRepeatingTasks(taskSaved.id!!)
            childRepeatingTasks.forEach {
                taskRepo.delete(it)
            }
        }
        taskRepo.deleteById(taskSaved.id!!)
    }
}