package com.trialbot.tasktest.models

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Embeddable
open class TaskUserKey(
    @Column(name = "taskid")
    open val taskId: Int,

    @Column(name = "userid")
    open val userId: Int
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
        var result = taskId
        result = 31 * result + userId
        return result
    }
}

@Entity
@Table(name = "task_to_user", schema = "public")
open class TaskUser(
    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "taskid")
    open val task: Task,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    open val user: User,

    @Column(nullable = false)
    open val date: LocalDateTime,

    @EmbeddedId
    open val id: TaskUserKey
)