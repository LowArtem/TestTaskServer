package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Priority
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tasks", schema = "public")
open class Task (
    @Column(nullable = false)
    open val name: String,

    @Column(nullable = false)
    open val deadline: LocalDateTime,

    @Column(nullable = false)
    open val status: Boolean = false,

    @Column(nullable = false)
    open val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Column(nullable = false)
    open val priority: Int = Priority.NORMAL.ordinal,

    @Column(nullable = true)
    open val description: String? = null,

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    open val users: Set<TaskUser>,

    // TODO: реализовать данные таблицы
    @Column(nullable = true, name = "groupeventid")
    open val groupEvent: Int? = null,

    @Column(nullable = true, name = "usereventid")
    open val userEvent: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class TaskDto(
    val name: String,

    val deadline: LocalDateTime,

    val status: Boolean = false,

    val difficulty: Int = Difficulty.NORMAL.ordinal,

    val priority: Int = Priority.NORMAL.ordinal,

    val description: String? = null,

    val id: Int? = null
)

fun Task.toDto(): TaskDto = TaskDto(name, deadline, status, difficulty, priority, description, id)

