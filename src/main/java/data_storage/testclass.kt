package data_storage

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.sql.DriverManager

fun main(args: Array<String>) {
    //an example connection to H2 DB
   Database.connect("jdbc:h2:./cities.db", driver = "org.h2.Driver")
//    Class.forName("org.sqlite.JDBC")
//    Database.connect("jdbc:sqlite:cities.db", driver = "org.sqlite.JDBC")

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)

        SchemaUtils.create (Cities)

        // insert new city. SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg')
        val stPeteId = Cities.insert {
            it[name] = "St. Petersburg"
        } get Cities.id

        // 'select *' SQL: SELECT Cities.id, Cities.name FROM Cities
        println("Cities: ${Cities.selectAll()}")
    }
}

object Cities: IntIdTable() {
    val name = varchar("name", 50)
}