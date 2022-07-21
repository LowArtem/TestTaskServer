package com.trialbot.tasktest.models

import javax.persistence.*

@Entity
@Table(name = "users", schema = "public")
open class User(
    @Column(nullable = false, unique = true)
    open val username: String,

    @Column(nullable = false)
    open val password: String,

    @Column(nullable = false)
    open val experience: Long = 0,

    @Column(nullable = false)
    open val money: Long = 0,

    @ManyToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "groupid", nullable = true)
    open val group: Group? = null,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    open val taskUsers: Set<TaskUser> = setOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int? = null
)

data class UserDto(
    val username: String,
    val password: String,
    val experience: Long = 0,
    val money: Long = 0,
    val id: Int? = null
)

data class UserLoginRequest(
    val username: String,
    val password: String
)

data class UserRegisterRequest(
    val username: String,
    val password: String
)

data class UserLoginResponse(
    val username: String,
    val password: String,
    val token: String,
    val experience: Long = 0,
    val money: Long = 0,
    val id: Int? = null
)

fun User.toDto(): UserDto = UserDto(username, password, experience, money, id)

