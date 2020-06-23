package handlers

import com.fasterxml.jackson.annotation.JsonProperty
import com.sun.net.httpserver.HttpExchange
import dao.Role
import dao.User
import dao.UserTable
import http_server.Server
import org.apache.commons.codec.digest.DigestUtils
import utils.JWTS
import java.lang.Exception

data class Token(@JsonProperty(Server.AUTHORIZATION_HEADER) val token:String)

class UserException(message: String, code: Int) : ServerException(message,code)

object LoginHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val user = mapper.readValue(exchange.requestBody, User::class.java)
        user.password = DigestUtils.md5Hex(user.password)
        val tableUser = UserTable.byLogin(user.login) ?:    throw UserException("no user with login '${user.login}'",401)
        if(tableUser.password!= user.password)              throw UserException("Incorrect password for this user", 401)
        writeResponse(exchange, 200, Token(JWTS.createJwt(tableUser)))

    }
}
object SignUpHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        val user = mapper.readValue(exchange.requestBody, User::class.java)
        user.password = DigestUtils.md5Hex(user.password)
        if(UserTable.byLogin(user.login)!= null)            throw UserException("User already exists",409)
        user.id = UserTable.insert(user)
        user.role = Role.User
        writeResponse(exchange,200, Token(JWTS.createJwt(user)))
    }

}

object ChangeUserRoleHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        val user = mapper.readValue(exchange.requestBody, User::class.java)
        UserTable.updateRole(user)
        writeResponse(exchange,204)
    }
}
object DeleteUserHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        val id = id(exchange)
        if(!UserTable.hasId(id))                            throw UserException("No user with id $id", 404)
        UserTable.delete(id)
        writeResponse(exchange,204)
    }
}

object GetAllUsersHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        writeResponse(exchange,200, UserTable.getAll())
    }
}

object OptionsHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
        exchange.responseHeaders.add( "Access-Control-Allow-Methods","POST, GET, OPTIONS, DELETE, PUT")
        exchange.responseHeaders.add( "Access-Control-Allow-Headers","X-Requested-With, Content-Type, Origin, Authorization, Accept, Client-Security-Token, Accept-Encoding, X-Auth-Token, content-type, access-control-allow-origin, authorization_token")
        exchange.sendResponseHeaders(200, -1)
        exchange.close()
    }
}

object TokenHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val token = exchange.requestHeaders.getFirst(Server.AUTHORIZATION_HEADER) ?: throw ServerException("Permission denied: token not found", 403)
        val claims = JWTS.decodeJwt(token)!!
        if(JWTS.isExpired(claims)) throw Exception("Token is expired")
        val role = JWTS.role(claims)
        val login = JWTS.login(claims)
        writeResponse(exchange, 200, User(login = login, role = role))
    }
}
