package com.trialbot.tasktest.features.crud.task

import com.trialbot.tasktest.models.TaskReceiveDto
import com.trialbot.tasktest.models.TaskStatusReceiveDto
import com.trialbot.tasktest.models.TaskUpdateReceiveDto
import com.trialbot.tasktest.utils.getToken
import com.trialbot.tasktest.utils.perform
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping("")
    fun getTasksByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val tasks = taskService.getTasksByUser(token.getToken() ?: "")
            ResponseEntity.ok().body(tasks)
        }
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            val task = taskService.getTask(id)
            ResponseEntity.ok().body(task)
        }
    }

    @PostMapping("/add")
    fun addTask(
        @RequestHeader(name="Authorization") token: String,
        @RequestBody habit: TaskReceiveDto
    ): ResponseEntity<*> {
        return perform {
            val taskCreated = taskService.addTask(token.getToken() ?: "", habit)
            ResponseEntity.ok().body(taskCreated)
        }
    }

    @PostMapping("/add/repeat")
    fun addTaskRepeat(@RequestBody taskId: Int): ResponseEntity<*> {
        return perform {
            val task = taskService.addTaskRepeat(taskId)
            ResponseEntity.ok().body(task)
        }
    }

    @PutMapping("/update")
    fun updateTask(@RequestBody task: TaskUpdateReceiveDto): ResponseEntity<*> {
        return perform {
            val taskUpdated = taskService.updateTask(task)
            ResponseEntity.ok().body(taskUpdated)
        }
    }

    @PutMapping("/update/status")
    fun updateTaskStatus(@RequestBody taskStatusReceive: TaskStatusReceiveDto): ResponseEntity<*> {
        return perform {
            taskService.updateTaskStatus(taskStatusReceive.taskId, taskStatusReceive.status)
            ResponseEntity.ok().body("Successfully updated")
        }
    }

    @PutMapping("/update/subtask/status")
    fun updateSubtaskStatus(@RequestBody taskStatusReceive: TaskStatusReceiveDto): ResponseEntity<*> {
        return perform {
            taskService.updateSubtaskStatus(taskStatusReceive)
            ResponseEntity.ok().body("Successfully updated")
        }
    }

    @DeleteMapping("/delete")
    fun deleteTask(@RequestBody taskId: Int): ResponseEntity<*> {
        return perform {
            taskService.deleteTask(taskId)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @DeleteMapping("/delete/subtask")
    fun deleteSubtask(@RequestBody subtaskId: Int): ResponseEntity<*> {
        return perform {
            taskService.deleteSubtask(subtaskId)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }
}