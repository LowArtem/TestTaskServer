package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Priority
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "tasks", schema = "public")
open class Task (
    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var deadline: Instant,

    @Column(nullable = false)
    open var status: Boolean = false,

    @Column(nullable = false)
    open var difficulty: Int = Difficulty.NORMAL.ordinal,

    @Column(nullable = false)
    open var priority: Int = Priority.NORMAL.ordinal,

    @Column(nullable = true)
    open var description: String? = null,

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    open val taskUsers: Set<TaskUser> = setOf(),

    // TODO: реализовать данные таблицы
    @Column(nullable = true, name = "groupeventid")
    open val groupEvent: Int? = null,

    @Column(nullable = true, name = "usereventid")
    open val userEvent: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (name != other.name) return false
        if (deadline != other.deadline) return false
        if (status != other.status) return false
        if (difficulty != other.difficulty) return false
        if (priority != other.priority) return false
        if (description != other.description) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + deadline.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + difficulty
        result = 31 * result + priority
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (id ?: 0)
        return result
    }
}

data class TaskReceiveDto(
    val name: String,
    val deadline: Instant,
    val difficulty: Int,
    val priority: Int,
    val description: String? = null
)

data class TaskResponseDto(
    var name: String,
    var deadline: Instant,
    var status: Boolean = false,
    var difficulty: Int,
    var priority: Int,
    var description: String?,
    val id: Int? = null
)

data class TaskStatusReceiveDto(
    val taskId: Int,
    val status: Boolean
)

fun Task.toResponseDto(): TaskResponseDto = TaskResponseDto(name, deadline, status, difficulty, priority, description, id)

