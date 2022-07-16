package com.trialbot.tasktest.models

import javax.persistence.*

@Entity
@Table(name = "users", schema = "public")
class User(
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val experience: Long = 0,

    @Column(nullable = false)
    val money: Long = 0,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "groupid", nullable = true)
    val group: Group? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)

