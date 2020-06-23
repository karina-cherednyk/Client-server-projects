package utils

import dao.Role
import dao.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.lang.Exception
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec


object JWTS {
    val algorithm = SignatureAlgorithm.HS256
   // val SECRET_KEY =  Keys.secretKeyFor(algorithm)
    val key = "secret key for client server application"
    val SECRET_KEY = key.toByteArray()
    val signingKey: Key = SecretKeySpec(SECRET_KEY, algorithm.jcaName)
    val days = 1

    fun createJwt(user: User): String {
        val buider = Jwts.builder().setId(user.id.toString())
                    .setIssuedAt(Date())
                    .claim("login", user.login)
                    .claim("role", user.role.name)
                    .signWith( signingKey)
                    .setExpiration(Date(System.currentTimeMillis()+24*3_600_000*days))
        return buider.compact()
    }
    @Throws(Exception::class)
    fun decodeJwt(jwt: String): Claims {
        return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY).build()
                    .parseClaimsJws(jwt).body?: throw Exception("no claims")

    }
    @Throws(Exception::class)
    fun role(claims: Claims): Role {
        val role = claims["role"]
        if(role == null) throw Exception("no role claim")
        else return Role.valueOf(role.toString())
    }
    @Throws(Exception::class)
    fun login(claims: Claims): String {
        val login = claims["login"]
        if(login == null) throw Exception("no login claim")
        else return login.toString()
    }
    fun isExpired(claims: Claims):Boolean =  claims.expiration.before(Date())
}
