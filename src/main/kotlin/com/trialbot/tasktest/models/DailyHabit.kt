package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "daily_habits", schema = "public")
open class DailyHabit(
    @Column(nullable = false)
    open val name: String,

    @Column(nullable = false)
    open val category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    open val user: User,

    @Column(nullable = false)
    open val deadline: LocalTime,

    @Column(nullable = false)
    open val status: Boolean = false,

    @Column(nullable = true)
    open val description: String? = null,

    @Column(nullable = false)
    open val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

