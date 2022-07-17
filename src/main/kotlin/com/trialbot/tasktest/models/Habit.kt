package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Type
import javax.persistence.*

@Entity
@Table(name = "habits", schema = "public")
open class Habit(
    @Column(nullable = false)
    open val name: String,

    @Column(nullable = false)
    open val category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    open val user: User,

    @Column(nullable = false)
    open val type: Int = Type.POSITIVE.ordinal,

    @Column(nullable = true)
    open val description: String? = null,

    @Column(nullable = false)
    open val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

