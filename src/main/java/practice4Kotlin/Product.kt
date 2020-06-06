package practice4Kotlin

data class Product  constructor(val id: Int, val name: String, var price: Double, var quantity: Int = 0) {

    @Synchronized
    fun addQuantity(quantity: Int) {
        this.quantity += quantity
    }

    @Synchronized
    fun reduceQuantity(quantity: Int) {
        if (quantity > quantity) throw IllegalArgumentException("Incorrect quantity")
        this.quantity -= quantity
    }
}
