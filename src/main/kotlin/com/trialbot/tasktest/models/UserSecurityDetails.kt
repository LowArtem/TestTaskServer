package com.trialbot.tasktest.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserSecurityDetails(private val user: UserDto) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(GrantedAuthority { "user" })

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}