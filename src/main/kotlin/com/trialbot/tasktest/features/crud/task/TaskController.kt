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
            val tasks = taskService.getTasksByUser(token.getToken() ?: "").sortedBy { it.id }
            ResponseEntity.ok().body(tasks)
        }
    }

    @GetMapping("/completed")
    fun getCompletedTasksByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val tasks = taskService.getCompletedTasksByUser(token.getToken() ?: "")
            ResponseEntity.ok().body(tasks)
        }
    }

    @GetMapping("/uncompleted")
    fun getUncompletedTasksByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val tasks = taskService.getUncompletedTasksByUser(token.getToken() ?: "").sortedBy { it.id }
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

    @PostMapping("/add/repeat/{task_id}")
    fun addTaskRepeat(@PathVariable task_id: Int): ResponseEntity<*> {
        return perform {
            val task = taskService.addTaskRepeat(task_id)
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
    fun updateTaskStatus(
        @RequestHeader(name = "Authorization") token: String,
        @RequestBody taskStatusReceive: TaskStatusReceiveDto
    ): ResponseEntity<*> {
        return perform {
            taskService.updateTaskStatus(taskStatusReceive.taskId, taskStatusReceive.status, token.getToken() ?: "")
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

    @DeleteMapping("/delete/{id}")
    fun deleteTask(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            taskService.deleteTask(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @DeleteMapping("/delete/subtask/{id}")
    fun deleteSubtask(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            taskService.deleteSubtask(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }
}