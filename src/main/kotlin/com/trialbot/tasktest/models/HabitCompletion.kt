package com.trialbot.tasktest.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "habit_completions", schema = "public")
class HabitCompletion(
    @Column(nullable = false)
    val date: LocalDateTime,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "habitid", nullable = false)
    val habit: Habit,

    @Column(nullable = false)
    val rating: Int = 5,

    @Column(nullable = false)
    val isPositive: Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

