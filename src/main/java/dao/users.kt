package dao


import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

enum class Role { User, Admin, Anonymous}
object UserTable: IntIdTable(){

    var login = varchar("login",50).uniqueIndex()
    var password = varchar("password", 250)
    var role = enumeration("role", Role::class)

    fun byId(id: Int) = transaction{ select { UserTable.id eq id }.singleOrNull()?.mapUser()}
    fun byLogin(login: String) = transaction{ select { UserTable.login eq login }.singleOrNull()?.mapUser()}

    fun insert(user: User) =
            transaction{ insertAndGetId { it[login] = user.login; it[password] = user.password; it[role] = user.role }.value}

    private fun ResultRow.mapUser() = User(this[id].value, this[login], this[password], this[role])
}
data class User(var id: Int? = null, var login:String, var password: String, var role: Role = Role.Anonymous)