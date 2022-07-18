package com.trialbot.tasktest.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "daily_completions", schema = "public")
open class DailyHabitCompletion(
    @Column(nullable = false)
    open val date: LocalDateTime,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyid", nullable = false)
    open val dailyHabit: DailyHabit,

    @Column(nullable = false)
    open val rating: Int = 5,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class DailyHabitCompletionDto(
    val date: LocalDateTime,
    val rating: Int = 5,
    val id: Int? = null
)

fun DailyHabitCompletion.toDto(): DailyHabitCompletionDto = DailyHabitCompletionDto(date, rating, id)