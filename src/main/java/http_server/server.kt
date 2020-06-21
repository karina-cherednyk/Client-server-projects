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
import org.apache.commons.codec.digest.DigestUtils
import utils.JWTS
import java.io.File
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.URI
import java.nio.file.Paths
import java.util.*


object Server {
    const val AUTHORIZATION_HEADER = "authorization_token"
    const val  PORT = 8080
    const val BACKLOG = 0
    val anonymous = HttpPrincipal("anonymous","anonymous")
    private val publicBinders = listOf(
            UriBinder(Method.GET, "/api/show/good/\\d+", GetProductHandler),
            UriBinder(Method.GET, "/api/show/categories", GetAllCategories),
            UriBinder(Method.GET, "/api/show/goods", GetAllProducts),
            UriBinder(Method.GET, "/api/show/category/\\d+", GetCategoryHandler)
    )
    private val anonBinders = listOf(
            UriBinder(Method.PUT, "/login", LoginHandler),
            UriBinder(Method.PUT, "/signup", SignUpHandler),
            UriBinder(Method.OPTIONS, "/", OptionsHandler)
    )
    private val adminBinders = listOf(
            UriBinder(Method.PUT, "/api/good", PutProductHandler),
            UriBinder(Method.POST, "/api/good", PostProductHandler),
            UriBinder(Method.DELETE, "/api/good/\\d+", DeleteProductHandler),
            UriBinder(Method.PUT, "/api/category", PutCategoryHandler),
            UriBinder(Method.POST, "/api/category", PostCategoryHandler),
            UriBinder(Method.DELETE, "/api/category/\\d+", DeleteCategoryHandler),
            UriBinder(Method.OPTIONS, "/", OptionsHandler)

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
        startDB()
        server = HttpServer.create(InetSocketAddress(PORT), BACKLOG)
        createContext("/", anonBinders, AnonymousPageException::class.simpleName!!, anonymousAuthenticator)
        createContext("/api/", adminBinders, AdminPageException::class.simpleName!!, adminAuthenticator)
        createContext("/api/show", publicBinders, PublicPageException::class.simpleName!!, publicAuthenticator)

    }
    fun start() { server.start() ; println("Server started")}
    fun stop() { server.stop(1) ; println("Server stopped")}

    private fun createContext(path:String, binders: List<UriBinder>, className:String, authenticator: Authenticator) {
        server.createContext(path) {
            binders.find { binder -> binder.matches(it) }?.apply { handle(it); return@createContext }
            UriHandler.writeResponse(it, 404, ErrorResponse(className, "page not found", it.principal.realm))
        }.authenticator = authenticator
    }
    private fun startDB(){
        Database.connect("jdbc:h2:./store.db", driver = "org.h2.Driver")
        transaction {  SchemaUtils.drop(UserTable, CategoryTable, ProductTable) } //remove
        transaction { SchemaUtils.create(UserTable, CategoryTable, ProductTable) }
        UserTable.insert(User( login="admin", password = DigestUtils.md5Hex("admin"), role = Role.Admin))

        val fn =  Paths.get("").toFile()
        var c:Int = 0
        val r = Random()
        fn.absoluteFile.walk().maxDepth(1).forEach {
            val fileName = it.name
            if(it.isFile && fileName.endsWith(".txt")){
                CategoryTable.insert(Category(name=fileName.removeSuffix(".txt")))
                ++c
                it.readLines().forEach{
                    ProductTable.insert(Product(name=it, category = c, price = r.nextDouble() * 100, amount = r.nextInt(1000) ))
                }

            }
        }
    }
}


fun main(){
    Runtime.getRuntime().addShutdownHook(object : Thread() {    override fun run()  = Server.stop()     })
    Server.start()
}