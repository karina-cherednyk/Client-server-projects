package practice4Kotlin
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class InsertException(message:String?): Exception(message)
class UpdateException(message:String?): Exception(message)
class DeleteException(message:String?): Exception(message)
class SelectException(message: String?): Exception(message)

object ProductDao{
    val connection: Connection = DB.connection;
    const val tableName = "product"

    private fun ResultSet.toProduct(): Product = Product(getInt("id"), getString("name"), getDouble("price"), getInt("quantity"))


    fun populateDB() {
        val names = File("names.txt").useLines { it.toList() }
        names.forEach{
            insert(it, Math.random()*200)
        }

    }


    fun createTable(){
        try{
            val statement = connection.createStatement()
            val s = "create table if not exists '$tableName' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT unique , 'price' DECIMAL(10,3), 'quantity' NUMBER) "
            statement.execute(s)
            println("Table $tableName created")
            println()
        }catch (e: SQLException) {
            throw RuntimeException("Cant create table", e)
        }
    }

    fun insert(name: String, price: Double): Product {
        try {
            connection.prepareStatement(
                    "insert into $tableName ('name', 'price', 'quantity') values (?,?,0)", Statement.RETURN_GENERATED_KEYS).use { insert ->

                insert.setString(1, name)
                insert.setDouble(2, price)
                insert.execute()

                val resultSet = insert.generatedKeys
                val id = resultSet.getInt(1)
                return Product(id, name, price)
            }
        } catch (e: SQLException) {
            throw InsertException(e.message)
        }
    }

    fun update(p: Product) {
        val updateStatement = "update $tableName set name = ?, price = ? where id = ?"
        try {
            connection.prepareStatement(updateStatement).use { statement ->
                statement.setString(1, p.name)
                statement.setDouble(2, p.price)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw UpdateException(e.message)
        }
    }

    fun delete(p: Product) {
        val deleteStatement = "delete from $tableName where id = ?"
        try {
            connection.prepareStatement(deleteStatement).use { statement ->
                statement.setInt(1, p.id)
                statement.executeUpdate()
            }

        } catch (e: SQLException) {
            throw DeleteException(e.message)
        }
    }

    

    fun selectOneByName(name: String): Product? {
        val sqlQuery = "SELECT * FROM $tableName WHERE name = ?"
        try {
            connection.prepareStatement(sqlQuery).use { statement ->
                statement.setString(1, name)
                val resultSet = statement.executeQuery()
                return if (resultSet.next()) resultSet.toProduct() else null
            }
        } catch (sqlException: SQLException) {
            throw SelectException(sqlException.message )
        }
    }

    fun select(limit: Int, offset: Int): List<Product> {
        val sqlQuery = "SELECT * FROM $tableName LIMIT ?, ?"

        try {
                connection.prepareStatement(sqlQuery).use { statement ->
                statement.setInt(1, offset)
                statement.setInt(2, limit)

                val resultSet = statement.executeQuery(sqlQuery)
                return fromResultSet(resultSet)
            }
        } catch (sqlException: SQLException) {
            throw SelectException(sqlException.message )
        }

    }
    fun selectAll(): List<Product> {
        val sqlQuery = "SELECT * FROM $tableName"

        try {
            val statement = connection.createStatement()

            val resultSet = statement.executeQuery(sqlQuery)
            return fromResultSet(resultSet)
        } catch (sqlException: SQLException) {
            throw SelectException(sqlException.message )
        }
    }

    fun select(criteria: Criteria): List<Product> {
        val sqlQuery = "SELECT * FROM " + tableName + criteria.filter()

        try {
                connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery(sqlQuery)
                return fromResultSet(resultSet)
                  }
        } catch (sqlException: SQLException) {
            throw SelectException(sqlException.message )
        }

    }

    @Throws(SQLException::class)
    private fun fromResultSet(resultSet: ResultSet): List<Product> {
        val products = LinkedList<Product>()
        while (resultSet.next()) products.add(resultSet.toProduct())
        return products
    }

    fun truncate() {
        val sqlQuery = "DROP TABLE $tableName"
        try {
            connection.createStatement().use { statement ->
            statement.execute(sqlQuery)
            println("Table $tableName truncated")
            println()
        }
        } catch (e: SQLException) {
           throw RuntimeException("Unable to truncate", e)
        }

    }
}