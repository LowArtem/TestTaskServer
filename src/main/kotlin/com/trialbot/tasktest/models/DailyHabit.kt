package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "daily_habits", schema = "public")
class DailyHabit(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    val user: User,

    @Column(nullable = false)
    val deadline: LocalTime,

    @Column(nullable = false)
    val status: Boolean = false,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = false)
    val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

