package com.trialbot.tasktest.models

import javax.persistence.*

@Entity
@Table(name = "groups", schema = "public")
open class Group (
    @Column(nullable = false, unique = true)
    open val name: String,

    @Column(nullable = true)
    open val description: String? = null,

    @Column(nullable = false, name = "entrancefee")
    open val entranceFee: Int = 0,

    @Column(nullable = false)
    open val bank: Long = 0,

    @Column(nullable = false)
    open val experience: Long = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)