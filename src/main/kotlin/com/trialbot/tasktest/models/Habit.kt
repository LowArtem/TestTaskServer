package com.trialbot.tasktest.models

import com.trialbot.tasktest.models.enums.Difficulty
import com.trialbot.tasktest.models.enums.Type
import javax.persistence.*

@Entity
@Table(name = "habits", schema = "public")
class Habit(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val category: String,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    val user: User,

    @Column(nullable = false)
    val type: Int = Type.POSITIVE.ordinal,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = false)
    val difficulty: Int = Difficulty.NORMAL.ordinal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

