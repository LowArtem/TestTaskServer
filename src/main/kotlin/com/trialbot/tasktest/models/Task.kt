package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Priority
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tasks", schema = "public")
class Task (
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val deadline: LocalDateTime,

    @Column(nullable = false)
    val status: Boolean = false,

    @Column(nullable = false)
    val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Column(nullable = false)
    val priority: Int = Priority.NORMAL.ordinal,

    @Column(nullable = true)
    val description: String? = null,

    // TODO: реализовать данные таблицы
    @Column(nullable = true, name = "groupeventid")
    val groupEvent: Int? = null,

    @Column(nullable = true, name = "usereventid")
    val userEvent: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

