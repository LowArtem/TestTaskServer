package com.trialbot.tasktest.features.crud.dailyHabit

import com.trialbot.tasktest.models.DailyHabitCompletionReceiveDto
import com.trialbot.tasktest.models.DailyHabitReceiveDto
import com.trialbot.tasktest.models.DailyHabitResponseDto
import com.trialbot.tasktest.utils.getToken
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException


@RestController
@RequestMapping("/api/daily_habits")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class DailyHabitController(
    private val dailyHabitService: DailyHabitService
) {

    @GetMapping("")
    fun getDailyHabitsByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val habits = dailyHabitService.getDailyHabitsByUser(token.getToken() ?: "")
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

    @DeleteMapping("/delete")
    fun deleteDailyHabit(@RequestBody habitId: Int): ResponseEntity<*> {
        return perform {
            dailyHabitService.deleteDailyHabit(habitId)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @GetMapping("/completions")
    fun getDailyHabitCompletions(@RequestBody habitId: Int): ResponseEntity<*> {
        return perform {
            val completions = dailyHabitService.getDailyHabitCompletions(habitId)
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

    @DeleteMapping("/completions/delete")
    fun deleteDailyHabitCompletion(@RequestBody habitCompletionId: Int): ResponseEntity<*> {
        return perform {
            dailyHabitService.deleteDailyHabitCompletion(habitCompletionId)
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
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Unknown error: ${e.localizedMessage}")
        }
    }
}