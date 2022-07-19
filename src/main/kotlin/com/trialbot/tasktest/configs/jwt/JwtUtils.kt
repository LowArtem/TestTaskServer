package com.trialbot.tasktest.configs.jwt

import com.trialbot.tasktest.models.UserSecurityDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtUtils {

    private val expirationMs: Long = 2629800000

    private val jwtSecret: String = """I think you know. I think you know that I'm not one for letting go. 
        |It goes to show. Sometimes the ones we love, we tend to hurt the most""".trimMargin()

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal: UserSecurityDetails = authentication.principal as UserSecurityDetails
        return Jwts.builder().setSubject(userPrincipal.username).setIssuedAt(Date())
            .setExpiration(Date(Date().time + expirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret).compact()
    }

    fun validateJwtToken(jwt: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt)
            return true
        } catch (e: MalformedJwtException) {
            System.err.println(e.message)
        } catch (e: IllegalArgumentException) {
            System.err.println(e.message)
        }
        return false
    }

    fun getUserNameFromJwtToken(jwt: String?): String? {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body.subject
    }
}