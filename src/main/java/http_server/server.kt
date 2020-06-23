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
import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Paths
import java.util.*



object Server {
    const val AUTHORIZATION_HEADER = "authorization_token"
    const val  PORT = 8080
    const val BACKLOG = 0
    val anonymous = HttpPrincipal("anonymous","anonymous")


    private val anonBinders = listOf(
            UriBinder(Method.PUT, "/login", LoginHandler),
            UriBinder(Method.PUT, "/signup", SignUpHandler),
            UriBinder(Method.OPTIONS, "/.*", OptionsHandler)
    )
    private val userBinders = listOf(
            UriBinder(Method.GET, "/api/show/good/\\d+", GetProductHandler),
            UriBinder(Method.GET, "/api/show/categories", GetAllCategoriesHandler),
            UriBinder(Method.GET, "/api/show/goods", GetAllProductsHandler),
            UriBinder(Method.GET, "/api/show/category/\\d+", GetCategoryHandler),
            UriBinder(Method.OPTIONS, "/.*", OptionsHandler)
    )

    private val adminBinders = listOf(
            UriBinder(Method.PUT, "/api/good", PutProductHandler),
            UriBinder(Method.POST, "/api/good", PostProductHandler),
            UriBinder(Method.DELETE, "/api/good/\\d+", DeleteProductHandler),
            UriBinder(Method.PUT, "/api/category", PutCategoryHandler),
            UriBinder(Method.POST, "/api/category", PostCategoryHandler),
            UriBinder(Method.DELETE, "/api/category/\\d+", DeleteCategoryHandler),
            UriBinder(Method.OPTIONS, "/.*", OptionsHandler)

    )
    private val superAdminBinders = listOf(
            UriBinder(Method.POST, "/user", ChangeUserRoleHandler),
            UriBinder(Method.DELETE, "/user/\\d+", DeleteUserHandler),
            UriBinder(Method.GET, "/users", GetAllUsersHandler),
            UriBinder(Method.OPTIONS, "/.*", OptionsHandler)
    )

    private val anonymousAuthenticator = object : Authenticator(){
        override fun authenticate(exch: HttpExchange?): Result {
            return Success(anonymous)
        }
    }
    private val userAuthenticator = RoleAuthenticator { role: Role -> role >= Role.User }
    private val adminAuthenticator = RoleAuthenticator { role: Role ->  role >= Role.Admin}
    private val superAdminAuthenticator = RoleAuthenticator{ role: Role ->  role >=  Role.SuperAdmin}

    var server: HttpServer
    init {
        startDB()
        server = HttpServer.create(InetSocketAddress(PORT), BACKLOG)
        createContext("/", anonBinders, AnonymousPageException::class.simpleName!!, anonymousAuthenticator)
        createContext("/api/show", userBinders, PublicPageException::class.simpleName!!, userAuthenticator)
        createContext("/api/", adminBinders, AdminPageException::class.simpleName!!, adminAuthenticator)
        createContext("/user", superAdminBinders, AdminPageException::class.simpleName!!, superAdminAuthenticator)
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
        UserTable.insert(User( login="admin", password = DigestUtils.md5Hex("admin"), role = Role.SuperAdmin))



        val producers = File("randomData/Producers.txt").readLines()
        val ps = producers.size
        val r = Random()
        Paths.get("randomData").toFile().absoluteFile.walk().maxDepth(1).forEach {
            val fileName = it.name
            if(it.isFile && fileName.endsWith(".txt") && it.name != "Producers.txt"){
                val c = CategoryTable.insert(Category(name=fileName.removeSuffix(".txt")))
                it.readLines().forEach{
                    ProductTable.insert(Product(name=it, category = c, price = r.nextDouble() * 100, amount = r.nextInt(1000), producer = producers[r.nextInt(ps)] ))
                }

            }
        }
    }
}


fun main(){
    Runtime.getRuntime().addShutdownHook(object : Thread() {    override fun run()  = Server.stop()     })
    Server.start()
}
