
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.*
import practice4Kotlin.*
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Lab04Test {
    @BeforeEach
    fun before()  {  ProductDao.createTable() }
    @AfterEach
    fun after()  {  ProductDao.truncate() }
    @AfterAll
    internal fun afterAll() { DB.close()}


    @Test
    fun insertTest(){
        val names = File("names.txt").useLines { it.toList() }
        names.forEach{
            ProductDao.insert(it, Math.random() * 200)
        }
        val insertedProducts = ProductDao.selectAll()
        assertEquals(names.size, insertedProducts.size)
        names.forEachIndexed{
            i, name -> assertEquals(name, insertedProducts[i].name)
        }
    }

    @org.junit.Test(expected = InsertException::class)
    fun notUniqueName(){
            ProductDao.insert("apple", 12.0);
            ProductDao.insert("apple", 13.0)
    }

    @Test
    fun selectByName(){
        ProductDao.populateDB()
        val name = "Blueberry and oatbran muffins"
            val list = ProductDao.select(Criteria(like = name))
            assert(list.size == 1)
        assertEquals(name, list[0].name)
    }

    @Test
    fun selectByNameExplicitly(){
        ProductDao.populateDB()
        val name = "Blueberry and oatbran muffins"
        val p = ProductDao.selectOneByName(name)
        assertNotNull(p)
        assertEquals(name, p.name)
    }

    @Test
    fun selectLike(){
        ProductDao.populateDB()
        val like = "%s"
        val list = ProductDao.select(Criteria(like))
        println(list)
        list.forEach{ assertTrue(it.name.endsWith("s")) }
    }
    @Test
    fun selectFrom(){
        ProductDao.populateDB()
        val from = 100.0
        val list = ProductDao.select(Criteria(priceFrom = from) )
        assertTrue(list.all { product ->  product.price >= from })
    }
    @Test
    fun selectTo(){
        ProductDao.populateDB()
        val to = 150.0
        val list = ProductDao.select(Criteria(priceTo = to) )
        assertTrue(list.all { product ->  product.price <= to })
    }

    @Test
    fun selectBetween(){
        ProductDao.populateDB()
        val from = 100.0; val to = 150.0
        val list = ProductDao.select(Criteria(priceFrom =  from, priceTo = to) )
        assertTrue(list.all { product ->  product.price in from..to })
    }
    @Test
    fun testUpdate(){
        val p = ProductDao.insert("apple",5.0)
        println(ProductDao.selectOneByName("apple"))
        println(p)
        p.price = 10.0
        ProductDao.update(p)
        assertEquals(ProductDao.selectOneByName("apple")!!.price, 5.0, 0.01)
    }
    @org.junit.Test(expected = UpdateException::class)
    fun nothingToUpdate(){
        ProductDao.update(Product(1, "test", 12.3))
    }
    @Test
    fun testDelete(){
        val p = ProductDao.insert("apple",5.0)
        assertEquals(ProductDao.selectAll().size, 1)
        ProductDao.delete(p)
        assertTrue(ProductDao.selectAll().isEmpty())
    }
    @org.junit.Test(expected = DeleteException::class)
    fun nothingToDelete(){
        ProductDao.delete(Product(1, "test", 12.3))
    }
}