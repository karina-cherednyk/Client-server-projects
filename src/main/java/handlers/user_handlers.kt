package handlers

import com.fasterxml.jackson.annotation.JsonProperty
import com.sun.net.httpserver.HttpExchange
import dao.Role
import dao.User
import dao.UserTable
import http_server.Server
import org.apache.commons.codec.digest.DigestUtils
import utils.JWTS

data class Token(@JsonProperty(Server.AUTHORIZATION_HEADER) val token:String)

class UserException(message: String, code: Int) : ServerException(message,code)

object LoginHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val user = mapper.readValue(exchange.requestBody, User::class.java)
        user.password = DigestUtils.md5Hex(user.password)
        val tableUser = UserTable.byLogin(user.login) ?: throw UserException("no user with this login",401)
        if(tableUser.password!= user.password) throw UserException("incorrect password for this user", 401)
        writeResponse(exchange, 200, Token(JWTS.createJwt(tableUser)))

    }
}
object SignUpHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        val user = mapper.readValue(exchange.requestBody, User::class.java)
        user.password = DigestUtils.md5Hex(user.password)
        if(UserTable.byLogin(user.login)!= null) throw UserException("user already exists",409)
        user.id = UserTable.insert(user)
        user.role = Role.User
        writeResponse(exchange,200, Token(JWTS.createJwt(user)))
    }

}