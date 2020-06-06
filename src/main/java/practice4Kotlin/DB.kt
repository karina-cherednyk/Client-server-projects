package practice4Kotlin


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DB {
    const val DB_NAME = "store.db"
    val connection: Connection by lazy {  DriverManager.getConnection("jdbc:sqlite:$DB_NAME")  }

    fun close() {
        try {
            connection.close()

            println("Connection closed")
            println()
        } catch (ex: SQLException) {
            println(ex.message)
        }

    }
}
