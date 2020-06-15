package handlers

import com.sun.net.httpserver.HttpExchange
import dao.Category
import dao.CategoryTable
import dao.Product
import dao.ProductTable

class ProductException(message: String, code: Int): ServerException(message, code)
class CategoryException(message: String, code: Int): ServerException(message, code)

object PutProductHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val product = mapper.readValue(exchange.requestBody, Product::class.java)
        if(!product.isValid())                          throw ProductException("Invalid product", 409)
        if(ProductTable.hasName(product.name) )         throw ProductException( "product with name ${product.name} already exists", 409)
        if(!CategoryTable.hasId(product.category))      throw CategoryException("category with id ${product.category} not found", 404)
        val id = ProductTable.insert(product)
        writeResponse(exchange,200, id )
    }
}

object PostProductHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val product = mapper.readValue(exchange.requestBody, Product::class.java)
        if(!product.isValid())                          throw ProductException("Invalid product", 409)
        if(product.id == null)                          throw ProductException("Product id not found", 409)
        if(!ProductTable.hasId(product.id!!))             throw ProductException("Product not found", 404)
        ProductTable.update(product)
        writeResponse(exchange,200, product)
    }
}

object PutCategoryHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val category = mapper.readValue(exchange.requestBody, Category::class.java)
        if(CategoryTable.hasName(category.name))        throw CategoryException("category with name ${category.name} already exists", 409)
        val id = CategoryTable.insert(category)
        writeResponse(exchange,200, id)
    }
}

object DeleteProductHandler: UriHandler(){
    override fun handleOrThrow(exchange: HttpExchange) {
        val uri = exchange.requestURI.toString()
        val id = uri.substring(uri.lastIndexOf('/')+1).toInt()
        if(!ProductTable.hasId(id))                     throw ProductException("Product not found", 404)
        ProductTable.delete(id)
        writeResponse(exchange,204, id)
    }
}

object GetProductHandler: UriHandler() {
    override fun handleOrThrow(exchange: HttpExchange) {
        val uri = exchange.requestURI.toString()
        val id = uri.substring(uri.lastIndexOf('/')+1).toInt()
        val product = ProductTable.byId(id)?:           throw ProductException("product with id $id not found", 404)
        writeResponse(exchange,200, product)
    }
}