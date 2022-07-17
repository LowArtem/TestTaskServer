package com.trialbot.tasktest.models

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

@Embeddable
class TaskUserKey(
    @Column(name = "taskid")
    val taskId: Int,

    @Column(name = "userid")
    val userId: Int
) : Serializable

@Entity
@Table(name = "task_to_user", schema = "public")
class TaskUser(

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "taskid")
    val task: Task,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    val user: User,

    @Column(nullable = false)
    val date: LocalDateTime,

    @EmbeddedId
    val id: TaskUserKey
)