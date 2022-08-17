package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Priority
import com.trialbot.tasktest.models.enums.RepeatingInterval
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "tasks", schema = "public")
open class Task (
    @Column(nullable = false)
    open var name: String,

    @Column(nullable = true)
    open var deadline: Instant?,

    @Column(nullable = false)
    open var status: Boolean = false,

    @Column(nullable = false)
    open var difficulty: Int = Difficulty.NORMAL.ordinal,

    @Column(nullable = false)
    open var priority: Int = Priority.NORMAL.ordinal,

    @Column(name = "repeatinginterval", nullable = false)
    open var repeatingInterval: Int = RepeatingInterval.NONE.ordinal,

    @Column(name = "parentrepeatingtaskid", nullable = true)
    open var parentRepeatingTask: Int? = null,

    @Column(nullable = true)
    open var description: String? = null,

    @Column(nullable = true)
    open var notification: Instant? = null,

    @OneToMany(mappedBy = "task", targetEntity = TaskUser::class, fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH])
    open var taskUsers: Set<TaskUser> = setOf(),

    @OneToMany(
        mappedBy = "parentTask",
        targetEntity = Subtask::class,
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH],
        orphanRemoval = true
    )
    open var subtasks: Set<Subtask> = setOf(),

    // TODO: реализовать данные таблицы
    @Column(nullable = true, name = "groupeventid")
    open val groupEvent: Int? = null,

    @Column(nullable = true, name = "usereventid")
    open val userEvent: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null
) : Cloneable  {
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

    public override fun clone(): Task {
        return Task(
            name = name,
            deadline = deadline,
            status = status,
            difficulty = difficulty,
            priority = priority,
            repeatingInterval = repeatingInterval,
            parentRepeatingTask = parentRepeatingTask,
            description = description,
            notification = notification,
            taskUsers = taskUsers,
            subtasks = subtasks,
            groupEvent = groupEvent,
            userEvent = userEvent,
            id = id
        )
    }
}

data class TaskReceiveDto(
    val name: String,
    val deadline: Instant?,
    val difficulty: Int,
    val priority: Int,
    val description: String? = null,
    val repeatingInterval: Int = RepeatingInterval.NONE.ordinal,
    val notification: Instant? = null,
    val subtasks: Set<SubtaskReceiveDto>? = null
)

data class TaskResponseDto(
    var name: String,
    var deadline: Instant?,
    var status: Boolean = false,
    var difficulty: Int,
    var priority: Int,
    var description: String?,
    var repeatingInterval: Int = RepeatingInterval.NONE.ordinal,
    var notification: Instant? = null,
    val id: Int? = null,
    var subtasks: Set<SubtaskResponseDto> = setOf()
)

data class TaskShortResponseDto(
    var name: String,
    var deadline: Instant?,
    var status: Boolean,
    var difficulty: Int,
    var priority: Int,
    var hasNotification: Boolean,
    var hasRepeat: Boolean,
    val id: Int,
)

data class TaskUpdateReceiveDto(
    var name: String,
    var deadline: Instant?,
    var status: Boolean = false,
    var difficulty: Int,
    var priority: Int,
    var description: String?,
    var repeatingInterval: Int = RepeatingInterval.NONE.ordinal,
    var notification: Instant? = null,
    val id: Int? = null,
    var subtasks: Set<SubtaskUpdateReceiveDto> = setOf()
)

data class TaskStatusReceiveDto(
    val taskId: Int,
    val status: Boolean
)

fun Task.toResponseDto(): TaskResponseDto = TaskResponseDto(
    name = name,
    deadline = deadline,
    status = status,
    difficulty = difficulty,
    priority = priority,
    description = description,
    repeatingInterval = repeatingInterval,
    notification = notification,
    id = id,
    subtasks = subtasks.map { it.toResponseDto() }.toSet()
)

fun Task.toShortResponseDto(): TaskShortResponseDto = TaskShortResponseDto(
    name = name,
    deadline = deadline,
    status = status,
    difficulty = difficulty,
    priority = priority,
    hasNotification = notification != null,
    hasRepeat = repeatingInterval != RepeatingInterval.NONE.ordinal,
    id = id ?: -1
)

fun Task.toUpdateReceiveDto(): TaskUpdateReceiveDto = TaskUpdateReceiveDto(
    name = name,
    deadline = deadline,
    status = status,
    difficulty = difficulty,
    priority = priority,
    description = description,
    repeatingInterval = repeatingInterval,
    notification = notification,
    id = id,
    subtasks = subtasks.map { it.toUpdateReceiveDto() }.toSet()
)
