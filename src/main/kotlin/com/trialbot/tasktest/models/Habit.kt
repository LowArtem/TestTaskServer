package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Type
import javax.persistence.*

@Entity
@Table(name = "habits", schema = "public")
open class Habit(
    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    open val user: User,

    @Column(nullable = false)
    open var type: Int = Type.POSITIVE.ordinal,

    @Column(nullable = true)
    open var description: String? = null,

    @Column(nullable = false)
    open var difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class HabitDto(
    val name: String,
    val category: String,
    val type: Int = Type.POSITIVE.ordinal,
    val description: String? = null,
    val difficulty: Int = Difficulty.NORMAL.ordinal,
    val id: Int? = null
)

fun Habit.toDto(): HabitDto = HabitDto(name, category, type, description, difficulty, id)

