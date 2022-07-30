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
    open var parentTask: Task,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null
) : Cloneable {
    public override fun clone(): Subtask = Subtask(
        text = text,
        status = status,
        parentTask = parentTask,
        id = id
    )
}

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