package com.ds.recipesapi.service

import com.ds.recipesapi.entity.Product
import com.ds.recipesapi.entity.Recipe
import com.ds.recipesapi.entity.RecipeIngredient
import com.ds.recipesapi.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class RecipeServiceTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val recipeService = RecipeService(recipeRepository)

    @Test
    fun `should return paginated recipes with mapped response`() = runBlocking {
        // Given
        val product = Product(id = 1, name = "Flour", priceInCents = 10000)
        val recipeIngredient = RecipeIngredient(
            id = 1,
            quantity = 500.0,
            unit = "g",
            recipe = mockk(),
            product = product
        )
        val recipe = Recipe(
            id = 1,
            name = "Test Recipe",
            ingredients = mutableListOf(recipeIngredient)
        )
        val page = PageImpl(listOf(recipe))
        val pageRequest = PageRequest.of(0, 10)

        every { recipeRepository.findAll(pageRequest) } returns page

        // When
        val result = recipeService.findAll(0, 10)

        // Then
        assertEquals(1, result.content.size)
        assertEquals(1, result.content[0].id)
        assertEquals("Test Recipe", result.content[0].name)
        assertEquals(1, result.content[0].ingredients.size)
        assertEquals("Flour", result.content[0].ingredients[0].productName)
        assertEquals(500.0, result.content[0].ingredients[0].quantity)
        assertEquals("g", result.content[0].ingredients[0].unit)

        verify { recipeRepository.findAll(pageRequest) }
    }

    @Test
    fun `should return empty page when no recipes found`() = runBlocking {
        // Given
        val emptyPage = PageImpl<Recipe>(emptyList())
        val pageRequest = PageRequest.of(0, 10)

        every { recipeRepository.findAll(pageRequest) } returns emptyPage

        // When
        val result = recipeService.findAll(0, 10)

        // Then
        assertEquals(0, result.content.size)
        verify { recipeRepository.findAll(pageRequest) }
    }

    @Test
    fun `should handle recipe with multiple ingredients`() = runBlocking {
        // Given
        val flour = Product(id = 1, name = "Flour", priceInCents = 10000)
        val sugar = Product(id = 2, name = "Sugar", priceInCents = 10000)
        
        val ingredient1 = RecipeIngredient(
            id = 1,
            quantity = 500.0,
            unit = "g",
            recipe = mockk(),
            product = flour
        )
        val ingredient2 = RecipeIngredient(
            id = 2,
            quantity = 200.0,
            unit = "g",
            recipe = mockk(),
            product = sugar
        )
        
        val recipe = Recipe(
            id = 1,
            name = "Complex Recipe",
            ingredients = mutableListOf(ingredient1, ingredient2)
        )
        val page = PageImpl(listOf(recipe))
        val pageRequest = PageRequest.of(0, 10)

        every { recipeRepository.findAll(pageRequest) } returns page

        // When
        val result = recipeService.findAll(0, 10)

        // Then
        assertEquals(1, result.content.size)
        assertEquals(2, result.content[0].ingredients.size)
        
        val ingredientNames = result.content[0].ingredients.map { it.productName }
        assertEquals(setOf("Flour", "Sugar"), ingredientNames.toSet())
    }
}
