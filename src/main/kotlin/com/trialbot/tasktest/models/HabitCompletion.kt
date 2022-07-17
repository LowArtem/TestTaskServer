package com.trialbot.tasktest.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "habit_completions", schema = "public")
open class HabitCompletion(
    @Column(nullable = false)
    open val date: LocalDateTime,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "habitid", nullable = false)
    open val habit: Habit,

    @Column(nullable = false)
    open val rating: Int = 5,

    @Column(nullable = false)
    open val isPositive: Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

