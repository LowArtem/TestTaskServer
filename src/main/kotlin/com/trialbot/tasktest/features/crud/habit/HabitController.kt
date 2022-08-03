package com.trialbot.tasktest.features.crud.habit

import com.trialbot.tasktest.models.HabitCompletionReceiveDto
import com.trialbot.tasktest.models.HabitReceiveDto
import com.trialbot.tasktest.models.HabitUpdateReceiveDto
import com.trialbot.tasktest.utils.getToken
import com.trialbot.tasktest.utils.perform
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class HabitController(
    private val habitService: HabitService
) {

    @GetMapping("")
    fun getHabitsByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val habits = habitService.getHabitsByUser(token.getToken() ?: "").sortedBy { it.id }
            ResponseEntity.ok().body(habits)
        }
    }

    @PostMapping("/add")
    fun addHabit(
        @RequestHeader(name = "Authorization") token: String,
        @RequestBody habit: HabitReceiveDto
    ): ResponseEntity<*> {
        return perform {
            val habitCreated = habitService.addHabit(token.getToken() ?: "", habit)
            ResponseEntity.ok().body(habitCreated)
        }
    }

    @PutMapping("/update")
    fun updateHabit(@RequestBody habit: HabitUpdateReceiveDto): ResponseEntity<*> {
        return perform {
            val habitUpdated = habitService.updateHabit(habit)
            ResponseEntity.ok().body(habitUpdated)
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteHabit(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            habitService.deleteHabit(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @GetMapping("/completions/{habit_id}")
    fun getHabitCompletions(@PathVariable habit_id: Int): ResponseEntity<*> {
        return perform {
            val completions = habitService.getHabitCompletions(habit_id).sortedBy { it.date }
            ResponseEntity.ok().body(completions)
        }
    }

    @PostMapping("/completions/add")
    fun addHabitCompletion(@RequestBody requestData: HabitCompletionReceiveDto): ResponseEntity<*> {
        return perform {
            val completionAdded = habitService.addHabitCompletion(requestData)
            ResponseEntity.ok().body(completionAdded)
        }
    }

    @DeleteMapping("/completions/delete/{id}")
    fun deleteHabitCompletion(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            habitService.deleteHabitCompletion(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }
}