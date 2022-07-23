package com.trialbot.tasktest.models

import java.io.Serializable
import java.time.Instant
import javax.persistence.*

@Embeddable
open class TaskUserKey(
    @Column(name = "taskid")
    open val taskId: Int? = null,

    @Column(name = "userid")
    open val userId: Int? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskUserKey

        if (taskId != other.taskId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = taskId ?: 0
        result = 31 * result + (userId ?: 0)
        return result
    }
}

@Entity
@Table(name = "task_to_user", schema = "public")
open class TaskUser(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "userid")
    open val user: User,

    @Column(nullable = true)
    open val date: Instant?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("taskId")
    @JoinColumn(name = "taskid")
    open val task: Task,

    @EmbeddedId
    open val id: TaskUserKey = TaskUserKey()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskUser

        if (task != other.task) return false
        if (user != other.user) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = task.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}