package com.trialbot.tasktest.models

import javax.persistence.*

@Entity
@Table(name = "subtasks", schema = "public")
open class Subtask (
    @Column(nullable = false)
    open var text: String,

    @Column(nullable = false)
    open var status: Boolean,

    @ManyToOne(cascade = [CascadeType.MERGE, CascadeType.REFRESH], fetch = FetchType.LAZY)
    @JoinColumn(name = "taskid", nullable = false)
    open val parentTask: Task,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class SubtaskReceiveDto(
    var text: String,
    var status: Boolean,
)

data class SubtaskUpdateReceiveDto(
    var text: String,
    var status: Boolean,
    val id: Int? = null,
)

data class SubtaskResponseDto(
    var text: String,
    var status: Boolean,
    val id: Int
)

fun Subtask.toResponseDto(): SubtaskResponseDto = SubtaskResponseDto(text, status, id ?: -1)

fun Subtask.toUpdateReceiveDto(): SubtaskUpdateReceiveDto = SubtaskUpdateReceiveDto(
    text = text,
    status = status,
    id = id
)