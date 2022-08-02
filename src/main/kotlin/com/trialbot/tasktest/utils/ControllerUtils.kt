package com.trialbot.tasktest.utils

import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.persistence.EntityNotFoundException

fun perform(operation: () -> ResponseEntity<*>): ResponseEntity<*> {
    return try {
        operation()
    }

    catch (_: EntityNotFoundException) {
        ResponseEntity.badRequest().body("Entity not found")
    }

    catch (_: MalformedJwtException) {
        ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed")
    }

    catch (_: UnsupportedOperationException) {
        ResponseEntity.internalServerError().body("Cannot execute this operation :(")
    }

    catch(e: IllegalStateException) {
        ResponseEntity.badRequest().body("You have passed wrong data: ${e.localizedMessage}")
    }

    catch (e: IllegalArgumentException) {
        ResponseEntity.status(HttpStatus.CONFLICT).body("You have passed wrong data: ${e.localizedMessage}")
    }

    catch (e: Exception) {
        ResponseEntity.internalServerError().body("Unknown error: ${e.localizedMessage}")
    }
}