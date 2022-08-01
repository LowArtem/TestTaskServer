package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.TaskReceiveDto
import com.trialbot.tasktest.models.TaskStatusReceiveDto
import com.trialbot.tasktest.models.TaskUpdateReceiveDto
import com.trialbot.tasktest.utils.getToken
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping("")
    fun getHabitsByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val habits = taskService.getTasksByUser(token.getToken() ?: "")
            ResponseEntity.ok().body(habits)
        }
    }

    @PostMapping("/add")
    fun addHabit(
        @RequestHeader(name="Authorization") token: String,
        @RequestBody habit: TaskReceiveDto
    ): ResponseEntity<*> {
        return perform {
            val habitCreated = taskService.addTask(token.getToken() ?: "", habit)
            ResponseEntity.ok().body(habitCreated)
        }
    }

    @PutMapping("/update")
    fun updateHabit(@RequestBody task: TaskUpdateReceiveDto): ResponseEntity<*> {
        return perform {
            val habitUpdated = taskService.updateTask(task)
            ResponseEntity.ok().body(habitUpdated)
        }
    }

    @PutMapping("/update/status")
    fun updateTaskStatus(@RequestBody taskStatusReceive: TaskStatusReceiveDto): ResponseEntity<*> {
        return perform {
            taskService.updateTaskStatus(taskStatusReceive.taskId, taskStatusReceive.status)
            ResponseEntity.ok().body("Successfully updated")
        }
    }

    @DeleteMapping("/delete")
    fun deleteHabit(@RequestBody taskId: Int): ResponseEntity<*> {
        return perform {
            taskService.deleteTask(taskId)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }



    private fun perform(operation: () -> ResponseEntity<*>): ResponseEntity<*> {
        return try {
            operation()
        } catch (_: EntityNotFoundException) {
            ResponseEntity.badRequest().body("Entity not found")
        } catch (_: MalformedJwtException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed")
        } catch (_: UnsupportedOperationException) {
            ResponseEntity.internalServerError().body("Cannot execute this operation :(")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Unknown error: ${e.localizedMessage}")
        }
    }
}