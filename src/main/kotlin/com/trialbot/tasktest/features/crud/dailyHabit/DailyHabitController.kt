package com.trialbot.tasktest.features.crud.dailyHabit

import com.trialbot.tasktest.models.DailyHabitCompletionReceiveDto
import com.trialbot.tasktest.models.DailyHabitReceiveDto
import com.trialbot.tasktest.models.DailyHabitResponseDto
import com.trialbot.tasktest.utils.getToken
import com.trialbot.tasktest.utils.perform
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/daily_habits")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class DailyHabitController(
    private val dailyHabitService: DailyHabitService
) {

    @GetMapping("")
    fun getDailyHabitsByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val habits = dailyHabitService.getDailyHabitsByUser(token.getToken() ?: "").sortedBy { it.id }
            ResponseEntity.ok().body(habits)
        }
    }

    @PostMapping("/add")
    fun addDailyHabit(
        @RequestHeader(name="Authorization") token: String,
        @RequestBody habit: DailyHabitReceiveDto
    ): ResponseEntity<*> {
        return perform {
            val habitCreated = dailyHabitService.addDailyHabit(token.getToken() ?: "", habit)
            ResponseEntity.ok().body(habitCreated)
        }
    }

    @PutMapping("/update")
    fun updateDailyHabit(@RequestBody habit: DailyHabitResponseDto): ResponseEntity<*> {
        return perform {
            val habitUpdated = dailyHabitService.updateDailyHabit(habit)
            ResponseEntity.ok().body(habitUpdated)
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteDailyHabit(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            dailyHabitService.deleteDailyHabit(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @GetMapping("/completions/{id}")
    fun getDailyHabitCompletions(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            val completions = dailyHabitService.getDailyHabitCompletions(id).sortedBy { it.date }
            ResponseEntity.ok().body(completions)
        }
    }

    @PostMapping("/completions/add")
    fun addDailyHabitCompletion(@RequestBody requestData: DailyHabitCompletionReceiveDto): ResponseEntity<*> {
        return perform {
            val completionAdded = dailyHabitService.addDailyHabitCompletion(requestData)
            ResponseEntity.ok().body(completionAdded)
        }
    }

    @DeleteMapping("/completions/delete/{id}")
    fun deleteDailyHabitCompletion(@PathVariable id: Int): ResponseEntity<*> {
        return perform {
            dailyHabitService.deleteDailyHabitCompletion(id)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }
}