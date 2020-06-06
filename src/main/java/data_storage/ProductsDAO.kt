package data_storage

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.sql.DriverManager


object Products: IntIdTable(){
    var title = varchar("name",50)
    var price = decimal("price", 18, 2)
    var amount = integer("amount")
}

class  Product(id: EntityID<Int>) : Entity<Int>(id){
    companion object: EntityClass<Int, Product>(Products)

    var title by Products.title
    var price by Products.price
    var amount by Products.amount
    override fun toString() = "title = $title, price = $price, amount = $amount"
}

fun  Query.filter(likeName: String?=null, from: BigDecimal?=null, to :BigDecimal?=null):List<ResultRow> {
    likeName?.let { andWhere { Products.title like it } }
    from?.let { andWhere { Products.price greaterEq  from } }
    to?.let {  andWhere { Products.price lessEq  to } }

    return sortedBy { Products.title }
}

fun main(){

    Database.connect("jdbc:h2:./store.db", driver = "org.h2.Driver")

    transaction {  SchemaUtils.create(Products) }

       val names = listOf("apple","banana","orange","pineapple","peach","coconut","cherry","lemon","watermelon")
        names.forEach{
            transaction {
                Product.new { title = it; price = (Math.random() * 1000).toBigDecimal(); amount = 0 }
            }
        }

    transaction {  Products.slice(Products.title, Products.price).selectAll().forEach{println(it)} }
    println("FILTER LIKE")
    transaction {  Products.slice(Products.title, Products.price).selectAll().filter("%an%").forEach{println(it)}}

    println("FILTER FROM TO")
    transaction {  Products.slice(Products.title, Products.price).selectAll().filter(from = 100.toBigDecimal(), to = 300.toBigDecimal()).forEach{println(it)}}

    println("UPDATE APPLE, SET PRICE =  5")
    transaction {
        val id = Products.update({Products.title eq "apple"}) { it[Products.price] = BigDecimal(5.0) }
        println(Product[id])
    }


    transaction { SchemaUtils.drop(Products) }
}