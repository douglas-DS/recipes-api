package com.ds.recipesapi.integration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
class DatabaseIntegrationTest(
    @Autowired private val dataSource: DataSource
) : BaseIntegrationTest() {

    @Test
    fun `should have H2 database configured`() {
        val connection = dataSource.connection
        val metaData = connection.metaData
        
        assertEquals("H2", metaData.databaseProductName)
        assertTrue(connection.isValid(5))
        
        connection.close()
    }

    @Test
    fun `should have all tables created`() {
        val connection = dataSource.connection
        val metaData = connection.metaData
        
        val expectedTables = listOf("CARTS", "PRODUCTS", "RECIPES", "RECIPE_INGREDIENTS", "CART_ITEMS")
        val resultSet = metaData.getTables(null, null, "%", arrayOf("TABLE"))
        
        val actualTables = mutableListOf<String>()
        while (resultSet.next()) {
            actualTables.add(resultSet.getString("TABLE_NAME"))
        }
        
        expectedTables.forEach { table ->
            assertTrue(actualTables.contains(table), "Table $table should exist")
        }
        
        resultSet.close()
        connection.close()
    }

    @Test
    fun `should have test data loaded`() {
        val connection = dataSource.connection
        
        // Check products count
        val productsStmt = connection.createStatement()
        val productsResult = productsStmt.executeQuery("SELECT COUNT(*) FROM products")
        productsResult.next()
        assertEquals(20, productsResult.getInt(1))
        
        // Check recipes count
        val recipesStmt = connection.createStatement()
        val recipesResult = recipesStmt.executeQuery("SELECT COUNT(*) FROM recipes")
        recipesResult.next()
        assertEquals(7, recipesResult.getInt(1))
        
        // Check carts count
        val cartsStmt = connection.createStatement()
        val cartsResult = cartsStmt.executeQuery("SELECT COUNT(*) FROM carts")
        cartsResult.next()
        assertEquals(5, cartsResult.getInt(1))
        
        // Check recipe ingredients
        val ingredientsStmt = connection.createStatement()
        val ingredientsResult = ingredientsStmt.executeQuery("SELECT COUNT(*) FROM recipe_ingredients")
        ingredientsResult.next()
        assertTrue(ingredientsResult.getInt(1) > 0)
        
        // Check cart items
        val cartItemsStmt = connection.createStatement()
        val cartItemsResult = cartItemsStmt.executeQuery("SELECT COUNT(*) FROM cart_items")
        cartItemsResult.next()
        assertTrue(cartItemsResult.getInt(1) > 0)
        
        connection.close()
    }

    @Test
    fun `should verify specific test data`() {
        val connection = dataSource.connection
        
        // Verify specific recipe exists
        val recipeStmt = connection.prepareStatement("SELECT name FROM recipes WHERE id = ?")
        recipeStmt.setInt(1, 1)
        val recipeResult = recipeStmt.executeQuery()
        recipeResult.next()
        assertEquals("Classic Chocolate Chip Cookies", recipeResult.getString("name"))
        
        // Verify cart with specific total
        val cartStmt = connection.prepareStatement("SELECT total_in_cents FROM carts WHERE id = ?")
        cartStmt.setInt(1, 1)
        val cartResult = cartStmt.executeQuery()
        cartResult.next()
        assertEquals(4730, cartResult.getLong("total_in_cents"))
        
        connection.close()
    }
}