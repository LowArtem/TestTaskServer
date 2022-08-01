package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Type
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "habits", schema = "public")
open class Habit(
    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    open val user: User,

    @Column(nullable = false)
    open var type: Int = Type.POSITIVE.ordinal,

    @Column(name = "lastnegativeactivationdate", nullable = true)
    open var lastNegativeActivationDate: Instant? = null,

    @Column(nullable = true)
    open var description: String? = null,

    @Column(nullable = false)
    open var difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null,
) {

    @OneToMany(mappedBy = "habit", cascade = [CascadeType.ALL], orphanRemoval = true)
    open val completions: List<HabitCompletion> = listOf()
}

data class HabitReceiveDto(
    val name: String,
    val category: String,
    val type: Int = Type.POSITIVE.ordinal,
    val description: String? = null,
    val difficulty: Int = Difficulty.NORMAL.ordinal,
)

data class HabitResponseDto(
    var name: String,
    var category: String,
    var type: Int = Type.POSITIVE.ordinal,
    var description: String? = null,
    var difficulty: Int = Difficulty.NORMAL.ordinal,
    var lastNegativeActivationDate: Instant? = null,
    val id: Int
)

fun Habit.toResponseDto(): HabitResponseDto = HabitResponseDto(
    name = name,
    category = category,
    type = type,
    description = description,
    difficulty = difficulty,
    lastNegativeActivationDate = lastNegativeActivationDate,
    id = id ?: -1
)

