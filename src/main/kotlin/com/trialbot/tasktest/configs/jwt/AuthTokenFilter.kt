package com.trialbot.tasktest.configs.jwt

import com.trialbot.tasktest.features.auth.UserAuthService
import com.trialbot.tasktest.utils.getToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthTokenFilter(
    private val userAuthService: UserAuthService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt: String? = parseJwt(request)
            if (jwt != null && JwtUtils.validateJwtToken(jwt)) {
                val username = JwtUtils.getUserNameFromJwtToken(jwt)
                val userDetails: UserDetails = userAuthService.loadUserByUsername(username)
                val authenticationToken =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        } catch (e: Exception) {
            System.err.println(e)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")
        return headerAuth.getToken()
    }
}