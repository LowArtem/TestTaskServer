package com.trialbot.tasktest.features.crud.habit

import com.trialbot.tasktest.models.HabitCompletionReceiveDto
import com.trialbot.tasktest.models.HabitReceiveDto
import com.trialbot.tasktest.models.HabitResponseDto
import com.trialbot.tasktest.utils.getToken
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException


@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class HabitController(
    private val habitService: HabitService
) {

    @GetMapping("")
    fun getHabitsByUser(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return perform {
            val habits = habitService.getHabitsByUser(token.getToken() ?: "")
            ResponseEntity.ok().body(habits)
        }
    }

    @PostMapping("/add")
    fun addHabit(
        @RequestHeader(name="Authorization") token: String,
        @RequestBody habit: HabitReceiveDto): ResponseEntity<*> {
        return perform {
            val habitCreated = habitService.addHabit(token.getToken() ?: "", habit)
            ResponseEntity.ok().body(habitCreated)
        }
    }

    @PutMapping("/update")
    fun updateHabit(@RequestBody habit: HabitResponseDto): ResponseEntity<*> {
        return perform {
            val habitUpdated = habitService.updateHabit(habit)
            ResponseEntity.ok().body(habitUpdated)
        }
    }

    @DeleteMapping("/delete")
    fun deleteHabit(@RequestBody habitId: Int): ResponseEntity<*> {
        return perform {
            habitService.deleteHabit(habitId)
            ResponseEntity.ok().body("Successfully deleted")
        }
    }

    @GetMapping("/completions")
    fun getHabitCompletions(@RequestBody habitId: Int): ResponseEntity<*> {
        return perform {
            val completions = habitService.getHabitCompletions(habitId)
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

    @DeleteMapping("/completions/delete")
    fun deleteHabitCompletion(@RequestBody habitCompletionId: Int): ResponseEntity<*> {
        return perform {
            habitService.deleteHabitCompletion(habitCompletionId)
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