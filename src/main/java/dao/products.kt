package dao

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


object CategoryTable: IntIdTable(){
    var name = varchar("name", 50).uniqueIndex()
    var description = text("description").nullable()

    fun ResultRow.mapCategory(): Category {
        val products = ProductTable.byCategory(this[id].value)
        val cost = products.sumByDouble { it.amount*it.price }
        return Category(this[id].value, this[name], this[description], products, cost)
    }

    fun ResultRow.mapCategoryPartly()
            = Category(this[id].value, this[name], this[description], products = null, totalCost = ProductTable.byCategory(this[id].value).sumByDouble { it.amount*it.price })

    fun hasId(id: Int) = transaction { !select{ CategoryTable.id eq id}.empty() }
    fun hasName(name: String) = transaction { !select{ CategoryTable.name eq name}.empty()}
    fun insert(category: Category) =
            transaction{
                insertAndGetId { it[name] = category.name; it[description] = category.description; }.value
            }
    fun byId(id: Int) = transaction { select{ CategoryTable.id eq id}.singleOrNull()?.mapCategory() }
    fun byName(name: String) = transaction { select{ CategoryTable.name eq name}.singleOrNull()?.mapCategory() }
    fun delete(id: Int) {
        ProductTable.deleteCategory(id)
        transaction { deleteWhere { CategoryTable.id eq id } }
    }
    fun getAll() = transaction { selectAll().map { it.mapCategoryPartly() } }

    fun update(category: Category) =
            transaction {   update({id eq category.id}) { it[name] = category.name; it[description] = category.description; }    }
}

object ProductTable: IntIdTable(){

    var name = varchar("name", 50).uniqueIndex()
    var description = text("description").nullable()
    val amount = integer("amount").clientDefault { 0 }
    val price = double("price")
    val category = reference("category", CategoryTable.id)


    private fun ResultRow.mapProduct() = Product(this[id].value, this[name], this[description], this[amount], this[price], this[category].value)
    fun byCategory(id: Int) = transaction { select{ category eq id} }.map { it.mapProduct() }
    fun byName(name: String) = transaction { select{ ProductTable.name eq name}.singleOrNull() ?.mapProduct() }
    fun hasName(name: String) = transaction { !select{ ProductTable.name eq name}.empty()}
    fun byId(id: Int) = transaction { select{ ProductTable.id eq id}.singleOrNull()?.mapProduct() }
    fun hasId(id: Int) = transaction { !select{ ProductTable.id eq id}.empty() }
    fun select(offset:Int, limit:Int) = transaction { selectAll().limit(limit, offset = offset).map { it.mapProduct() } }

    fun insert(product: Product) =
            transaction{
                insertAndGetId { it[name] = product.name; it[description] = product.description; it[amount] = product.amount; it[price]  = product.price; it[category] = EntityID(product.category, CategoryTable)}.value
            }

    fun update(product: Product) =
            transaction {
                update({id eq product.id}) { it[name] = product.name; it[description] = product.description; it[amount] = product.amount; it[price]  = product.price}
            }

    fun delete(id: Int) = transaction { deleteWhere { ProductTable.id eq id } }
    fun deleteCategory(id: Int) = transaction { deleteWhere { ProductTable.category eq id } }
}
data class Category(var id:Int?=null, var name:String, var description:String?=null,  var products: List<Product>?=null, var  totalCost:Double? = null)
data class Product(var id:Int?=null, var name:String, var description:String?=null, var amount:Int=0, var price:Double, var category:Int){
    @JsonIgnore
    fun isValid() = amount>=0 && price>=0
}