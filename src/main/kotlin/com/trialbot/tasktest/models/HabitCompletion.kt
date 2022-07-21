package com.trialbot.tasktest.models

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "habit_completions", schema = "public")
open class HabitCompletion(
    @Column(nullable = false)
    open val date: Instant,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "habitid", nullable = false)
    open val habit: Habit,

    @Column(nullable = false)
    open val rating: Int = 5,

    @Column(nullable = false, name = "ispositive")
    open val isPositive: Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class HabitCompletionDto(
    val date: Instant,
    val rating: Int,
    val isPositive: Boolean = true,
    val id: Int? = null
)

data class HabitCompletionReceiveDto(
    val habitId: Int,
    val date: Instant,
    val rating: Int,
    val isPositive: Boolean = true
)

fun HabitCompletion.toDto(): HabitCompletionDto = HabitCompletionDto(date, rating, isPositive, id)

