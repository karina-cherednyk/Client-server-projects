package utils

import dao.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.lang.Exception
import java.security.Key
import java.util.*
import javax.crypto.Cipher.SECRET_KEY
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
    fun role(jwt: Claims): String {
        val role = jwt["role"]
        if(role == null) throw Exception("no role claim")
        else return role.toString()
    }
    @Throws(Exception::class)
    fun login(jwt: Claims): String {
        val login = jwt["login"]
        if(login == null) throw Exception("no login claim")
        else return login.toString()
    }
    fun isExpired(claims: Claims):Boolean =  claims.expiration.before(Date())
}
