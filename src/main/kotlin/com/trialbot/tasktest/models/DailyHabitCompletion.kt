package com.trialbot.tasktest.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "daily_completions", schema = "public")
class DailyHabitCompletion(
    @Column(nullable = false)
    val date: LocalDateTime,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyid", nullable = false)
    val dailyHabit: DailyHabit,

    @Column(nullable = false)
    val rating: Int = 5,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)