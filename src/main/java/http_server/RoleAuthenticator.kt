package http_server

import dao.Role
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpPrincipal
import handlers.ErrorResponse
import handlers.Method
import handlers.UriHandler
import utils.JWTS
import java.lang.Exception

class RoleAuthenticator(val pass: (role: Role)->Boolean): Authenticator() {
    override fun authenticate(exchange: HttpExchange?): Result {
        if(exchange!!.requestMethod == Method.OPTIONS.name) return Success(Server.anonymous)
        return try {
            val token = exchange.requestHeaders.getFirst(Server.AUTHORIZATION_HEADER) ?: throw Exception("Permission denied: token not found")
            val claims = JWTS.decodeJwt(token)!!
            if(JWTS.isExpired(claims)) throw Exception("Token is expired")
            val role = JWTS.role(claims)
            if( !pass(role) ) throw Exception("Permission denied: role is not sufficient")
            Success(HttpPrincipal(JWTS.login(claims), role.name))
        } catch (e: Exception){
            UriHandler.writeResponse(exchange, 403, ErrorResponse(e::class.simpleName!!,e.message!!))
            Failure(403)
        }
    }

}