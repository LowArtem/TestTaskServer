package com.trialbot.tasktest.models

import javax.persistence.*

@Entity
@Table(name = "groups", schema = "public")
open class Group (
    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = false)
    val entranceFee: Int = 0,

    @Column(nullable = false)
    val bank: Long = 0,

    @Column(nullable = false)
    val experience: Long = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)