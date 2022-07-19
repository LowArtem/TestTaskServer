package com.trialbot.tasktest.configs

import com.trialbot.tasktest.configs.jwt.AuthEntryPointJwt
import com.trialbot.tasktest.configs.jwt.AuthTokenFilter
import com.trialbot.tasktest.configs.jwt.JwtUtils
import com.trialbot.tasktest.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userService: UserService,
    private val entryPointJwt: AuthEntryPointJwt,
    private val jwtUtils: JwtUtils,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(entryPointJwt).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests { configurer ->
                configurer
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/test/**").permitAll()
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = CustomPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider? {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userService)
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter? {
        return AuthTokenFilter(userService)
    }
}