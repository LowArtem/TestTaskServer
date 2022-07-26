package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "daily_habits", schema = "public")
open class DailyHabit(
    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    open val user: User,

    @Column(nullable = false)
    open var deadline: Instant?,

    @Column(nullable = false)
    open var status: Boolean = false,

    @Column(nullable = true)
    open var description: String? = null,

    @Column(nullable = false)
    open var difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
) {

    @OneToMany(mappedBy = "dailyHabit", cascade = [CascadeType.ALL], orphanRemoval = true)
    open val completions: List<DailyHabitCompletion> = listOf()
}

data class DailyHabitReceiveDto(
    val name: String,
    val category: String,
    val deadline: Instant?,
    val description: String?,
    val difficulty: Int = Difficulty.NORMAL.ordinal
)

data class DailyHabitResponseDto(
    val name: String,
    val category: String,
    val deadline: Instant?,
    val status: Boolean = false,
    val description: String? = null,
    val difficulty: Int = Difficulty.NORMAL.ordinal,
    val id: Int? = null
)

fun DailyHabit.toResponseDto(): DailyHabitResponseDto = DailyHabitResponseDto(name, category, deadline, status, description, difficulty, id)

