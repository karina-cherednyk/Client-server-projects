import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dao.*
import http_server.Server
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.text.IsEmptyString.emptyOrNullString

import io.restassured.RestAssured
import io.restassured.response.Response
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Practice5Test {

    data class Token(@JsonProperty(Server.AUTHORIZATION_HEADER) val token: String)
    val adminHex = User( login="admin", password = DigestUtils.md5Hex("admin"), role = Role.Admin)
    val admin = User( login="admin", password = "admin" , role = Role.Admin)
    val user = User(login="a",password = "b")

    @BeforeAll
    internal fun startConnection() {
        Database.connect("jdbc:h2:./store.db", driver = "org.h2.Driver")
        Server.start()
        RestAssured.port = Server.PORT
    }
    @AfterAll
    internal fun closeConnection() {
        Server.stop()
    }
    @BeforeEach
    fun init() {
        transaction { SchemaUtils.create(UserTable, CategoryTable, ProductTable) }
    }
    @AfterEach
    fun cleanUp()  {
        transaction { SchemaUtils.drop(UserTable, CategoryTable, ProductTable) }
    }

    fun signup(user: User) =
            given().body(user).`when`().put("/signup").then().statusCode(200).body(Server.AUTHORIZATION_HEADER, not(emptyOrNullString()))

    fun login(user: User) =
            given().body(user). `when`().put("/login").then().statusCode(200).body(Server.AUTHORIZATION_HEADER, not(emptyOrNullString()))




    fun token(response: Response): Token {
        return jacksonObjectMapper().readValue(response.body.asByteArray(), Token::class.java)
    }
    fun logInAndGetToken(user:User): Token {
        val response = given().body(user).put("/login")
        return token(response)
    }
    @Test
    fun shouldReturnTokenAfterSignUP() { signup(user) }

    @Test
    fun shouldSignUpThenLogin(){
        signup(user)
        login(user)
    }
    @Test
    fun shouldAddCategoryAsAdmin(){
        UserTable.insert(adminHex)
        val token = logInAndGetToken(admin).token
        given().header(Server.AUTHORIZATION_HEADER, token).body(Category(name="test")).`when`().put("/api/category").then().body(`is`("1"))
    }
    @Test
    fun shouldNotAddCategoryAsUser(){
        signup(user)
        val token = logInAndGetToken(user).token
        given().header(Server.AUTHORIZATION_HEADER, token).body(Category(name="test")).`when`().put("/api/category").then().statusCode(403)
    }
    @Test
    fun shouldNotAddCategoryAsUnauthorized(){
        given().body(Category(name="test")).`when`().put("/api/category").then().statusCode(403)
    }
    @Test
    fun createCategoryProductAndReturnIt(){
        val c = Category(name = "a", description = "b")

        UserTable.insert(adminHex)
        val token = logInAndGetToken(admin).token
        val cId = given().header(Server.AUTHORIZATION_HEADER,token).body(c).put("/api/category").body.asString().toInt()

        val p = Product(name="aa", description = "bb", category = cId, price = 12.0)

        val pId = given().header(Server.AUTHORIZATION_HEADER,token).body(p).put("/api/good").body.asString().toInt()
        p.id = pId
        val fetchedProduct = jacksonObjectMapper().readValue(given().header(Server.AUTHORIZATION_HEADER,token).get("/api/good/show/1").body.asByteArray(), Product::class.java)
        assert(p == fetchedProduct)
    }
}