package com.ds.recipesapi.service

import com.ds.recipesapi.entity.Cart
import com.ds.recipesapi.entity.CartItem
import com.ds.recipesapi.entity.Product
import com.ds.recipesapi.entity.Recipe
import com.ds.recipesapi.entity.RecipeIngredient
import com.ds.recipesapi.error.HttpException
import com.ds.recipesapi.repository.CartRepository
import com.ds.recipesapi.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CartServiceTest {

    private lateinit var cartRepository: CartRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var cartService: CartService

    @BeforeEach
    fun setUp() {
        cartRepository = mockk()
        recipeRepository = mockk()
        cartService = CartService(cartRepository, recipeRepository)
    }

    @Test
    fun `findOne should return cart response when cart exists`() = runTest {
        // Given
        val cartId = 1
        val cart = createMockCart()
        every { cartRepository.findById(cartId) } returns Optional.of(cart)

        // When
        val result = cartService.findOne(cartId)

        // Then
        assertEquals(cartId, result.id)
        assertEquals(cart.totalInCents, result.total)
        verify { cartRepository.findById(cartId) }
    }

    @Test
    fun `findOne should throw HttpException when cart not found`() = runTest {
        // Given
        val cartId = 999
        every { cartRepository.findById(cartId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<HttpException.NotFound> {
            cartService.findOne(cartId)
        }
        assertEquals("Cart with id $cartId not found", exception.message)
    }

    @Test
    fun `addRecipe should add recipe to cart and calculate total correctly`() = runTest {
        // Given
        val cartId = 1
        val recipeId = 1
        val cart = createMockCart(totalInCents = 1000L)
        val recipe = createMockRecipe()
        val savedCartSlot = slot<Cart>()

        // Create a saved cart that simulates what would be returned from the database
        // with CartItems that have proper IDs
        val savedCart = createMockCart(totalInCents = 1500L).apply {
            val cartItem = CartItem(id = 1, cart = this, recipe = recipe)
            items.add(cartItem)
        }

        every { cartRepository.findById(cartId) } returns Optional.of(cart)
        every { recipeRepository.findById(recipeId) } returns Optional.of(recipe)
        every { cartRepository.save(capture(savedCartSlot)) } returns savedCart

        // When
        val result = cartService.addRecipe(cartId, recipeId)

        // Then
        val capturedCart = savedCartSlot.captured

        // Verify that the recipe was added to the cart before saving
        assertEquals(1, capturedCart.items.size)
        val addedItem = capturedCart.items.first()
        assertEquals(recipe, addedItem.recipe)
        assertEquals(cart, addedItem.cart)

        // Verify total calculation: initial 1000 + (200 + 300) from recipe ingredients
        val expectedTotal = 1000L + recipe.ingredients.sumOf { it.product.priceInCents }
        assertEquals(expectedTotal, capturedCart.totalInCents)
        assertEquals(1500L, capturedCart.totalInCents) // 1000 + 200 + 300

        // Verify response (based on the savedCart returned from repository)
        assertEquals(cartId, result.id)
        assertEquals(1500L, result.total)
        assertEquals(1, result.items.size)

        verify { cartRepository.findById(cartId) }
        verify { recipeRepository.findById(recipeId) }
        verify { cartRepository.save(any()) }
    }

    @Test
    fun `addRecipe should throw HttpException when recipe not found`() = runTest {
        // Given
        val cartId = 1
        val recipeId = 999
        val cart = createMockCart()

        every { cartRepository.findById(cartId) } returns Optional.of(cart)
        every { recipeRepository.findById(recipeId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<HttpException.NotFound> {
            cartService.addRecipe(cartId, recipeId)
        }
        assertEquals("Recipe with id $recipeId not found", exception.message)
    }

    @Test
    fun `removeOneRecipe should remove recipe from cart successfully`() = runTest {
        // Given
        val cartId = 1
        val recipeId = 1
        val recipe = createMockRecipe(id = recipeId)
        val cartItem = CartItem(id = 1, cart = createMockCart(), recipe = recipe)
        val cart = createMockCart().apply {
            items.add(cartItem)
        }

        every { cartRepository.findById(cartId) } returns Optional.of(cart)
        every { cartRepository.save(cart) } returns cart

        // When
        cartService.removeOneRecipe(cartId, recipeId)

        // Then
        assertTrue(cart.items.isEmpty())
        verify { cartRepository.findById(cartId) }
        verify { cartRepository.save(cart) }
    }

    @Test
    fun `removeOneRecipe should throw HttpException when cart item not found`() = runTest {
        // Given
        val cartId = 1
        val recipeId = 999
        val cart = createMockCart() // Empty cart

        every { cartRepository.findById(cartId) } returns Optional.of(cart)

        // When & Then
        val exception = assertThrows<HttpException.NotFound> {
            cartService.removeOneRecipe(cartId, recipeId)
        }
        assertEquals("Cart item with recipe id $recipeId not found", exception.message)
    }

    @Test
    fun `Cart addRecipe function should calculate total correctly with multiple ingredients`() {
        // Given
        val cart = Cart(id = 1, totalInCents = 500L)
        val product1 = Product(id = 1, name = "Ingredient 1", priceInCents = 150L)
        val product2 = Product(id = 2, name = "Ingredient 2", priceInCents = 250L)
        val product3 = Product(id = 3, name = "Ingredient 3", priceInCents = 100L)
        
        val ingredient1 = RecipeIngredient(id = 1, quantity = 1.0, unit = "cup", recipe = mockk(), product = product1)
        val ingredient2 = RecipeIngredient(id = 2, quantity = 2.0, unit = "tbsp", recipe = mockk(), product = product2)
        val ingredient3 = RecipeIngredient(id = 3, quantity = 0.5, unit = "kg", recipe = mockk(), product = product3)
        
        val recipe = Recipe(id = 1, name = "Test Recipe").apply {
            ingredients.addAll(listOf(ingredient1, ingredient2, ingredient3))
        }

        val initialTotal = cart.totalInCents

        // When
        cart.addRecipe(recipe)

        // Then
        val expectedIncrease = product1.priceInCents + product2.priceInCents + product3.priceInCents
        val expectedTotal = initialTotal + expectedIncrease
        
        assertEquals(expectedTotal, cart.totalInCents)
        assertEquals(1000L, cart.totalInCents) // 500 + 150 + 250 + 100
        assertEquals(1, cart.items.size)
        
        val addedItem = cart.items.first()
        assertEquals(recipe, addedItem.recipe)
        assertEquals(cart, addedItem.cart)
    }

    @Test
    fun `Cart addRecipe function should handle recipe with no ingredients`() {
        // Given
        val cart = Cart(id = 1, totalInCents = 500L)
        val recipe = Recipe(id = 1, name = "Empty Recipe") // No ingredients
        val initialTotal = cart.totalInCents

        // When
        cart.addRecipe(recipe)

        // Then
        assertEquals(initialTotal, cart.totalInCents) // Total should remain unchanged
        assertEquals(1, cart.items.size)
        
        val addedItem = cart.items.first()
        assertEquals(recipe, addedItem.recipe)
        assertEquals(cart, addedItem.cart)
    }

    private fun createMockCart(
        id: Int = 1,
        totalInCents: Long = 1000L
    ): Cart {
        return Cart(id = id, totalInCents = totalInCents)
    }

    private fun createMockRecipe(id: Int = 1): Recipe {
        val product1 = Product(id = 1, name = "Test Product 1", priceInCents = 200L)
        val product2 = Product(id = 2, name = "Test Product 2", priceInCents = 300L)
        
        val ingredient1 = RecipeIngredient(
            id = 1,
            quantity = 1.0,
            unit = "cup",
            recipe = mockk(),
            product = product1
        )
        val ingredient2 = RecipeIngredient(
            id = 2,
            quantity = 2.0,
            unit = "tbsp",
            recipe = mockk(),
            product = product2
        )
        
        return Recipe(id = id, name = "Test Recipe").apply {
            ingredients.addAll(listOf(ingredient1, ingredient2))
        }
    }
}