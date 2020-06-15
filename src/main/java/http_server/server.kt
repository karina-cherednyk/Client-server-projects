package http_server

import com.sun.net.httpserver.HttpServer
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpPrincipal
import dao.*
import handlers.*
import utils.JWTS
import java.lang.Exception
import java.net.InetSocketAddress


object Server {
    const val AUTHORIZATION_HEADER = "authorization_token"
    val anonymous = HttpPrincipal("anonymous","anonymous")
    private val publicBinders = listOf(
            UriBinder(Method.GET, "/api/good/show/\\d+", GetProductHandler)
    )
    private val anonBinders = listOf(
            UriBinder(Method.PUT, "/login", LoginHandler),
            UriBinder(Method.PUT, "/signup", SignUpHandler)
    )
    private val adminBinders = listOf(
            UriBinder(Method.PUT, "/api/good", PutProductHandler),
            UriBinder(Method.POST, "/api/good", PostProductHandler),
            UriBinder(Method.DELETE, "/api/good/\\d+", DeleteProductHandler),
            UriBinder(Method.PUT, "/api/category", PutCategoryHandler)
    )

    private val publicAuthenticator = object:Authenticator(){
        override fun authenticate(exchange: HttpExchange?): Result {
            val token = exchange!!.requestHeaders.getFirst(AUTHORIZATION_HEADER) ?: return Success(anonymous)
            return try {
                val claims = JWTS.decodeJwt(token)!!
                if(JWTS.isExpired(claims)) throw Exception("Token is expired")
                Success(HttpPrincipal(JWTS.login(claims), JWTS.role(claims)))
            } catch (e: Exception){
                UriHandler.writeResponse(exchange, 403, ErrorResponse(e::class.simpleName!!,e.message!!))
                Failure(403)
            }
        }
    }
    private val anonymousAuthenticator = object : Authenticator(){
        override fun authenticate(exch: HttpExchange?): Result {
            return Success(anonymous)
        }
    }
    private val adminAuthenticator = object: Authenticator(){
        override fun authenticate(exchange: HttpExchange?): Result {
            return try {
                val token = exchange!!.requestHeaders.getFirst(AUTHORIZATION_HEADER)
                val claims = JWTS.decodeJwt(token)!!
                if(JWTS.role(claims)!= Role.Admin.name) throw Exception("permission denied")
                Success(HttpPrincipal(JWTS.login(claims), JWTS.role(claims)))
            } catch (e: Exception){
                UriHandler.writeResponse(exchange!!, 403, ErrorResponse(e::class.simpleName!!,e.message!!))
                Failure(403)
            }
        }

    }

    var server: HttpServer
    init {
        Database.connect("jdbc:h2:./store.db", driver = "org.h2.Driver")
        transaction {  SchemaUtils.create(UserTable, ProductTable, CategoryTable) }

        server = HttpServer.create(InetSocketAddress(8080), 0)
        createContext("/", anonBinders, AnonymousPageException::class.simpleName!!, anonymousAuthenticator)
        createContext("/api/good", adminBinders, AdminPageException::class.simpleName!!, adminAuthenticator)
        createContext("/api/good/show", publicBinders, PublicPageException::class.simpleName!!, publicAuthenticator)

    }
    fun start() { server.start() ; println("Server started")}
    fun stop() { server.stop(1) ; println("Server stopped")}

    private fun createContext(path:String, binders: List<UriBinder>, className:String, authenticator: Authenticator) {
        server.createContext(path) {
            binders.find { binder -> binder.matches(it) }?.apply { handle(it); return@createContext }
            UriHandler.writeResponse(it, 404, ErrorResponse(className, "page not found", it.principal.realm))
        }.authenticator = authenticator
    }
}


fun main(){
    Runtime.getRuntime().addShutdownHook(object : Thread() {    override fun run()  = Server.stop()     })

//    val admin = User( login="admin", password = DigestUtils.md5Hex("admin"), role = Role.Admin)
    Server.start()
//    UserTable.insert(admin)

}