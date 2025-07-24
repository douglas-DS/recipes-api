package com.ds.recipesapi.controller

import com.ds.recipesapi.dto.RecipeIngredientResponse
import com.ds.recipesapi.dto.RecipeResponse
import com.ds.recipesapi.service.RecipeService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(RecipeController::class)
class RecipeControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var recipeService: RecipeService

    @Test
    fun `should return recipes with default pagination`() {
        // Given
        val recipeResponse = RecipeResponse(
            id = 1,
            name = "Test Recipe",
            ingredients = listOf(
                RecipeIngredientResponse(
                    id = 1,
                    productName = "Flour",
                    quantity = 500.0,
                    unit = "g"
                )
            )
        )
        val pageRequest = PageRequest.of(0, 10)
        val expectedPage = PageImpl(listOf(recipeResponse), pageRequest, 1)

        coEvery { recipeService.findAll(0, 10) } returns expectedPage

        // When & Then
        webTestClient.get()
            .uri("/recipes")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content[0].id").isEqualTo(1)
            .jsonPath("$.content[0].name").isEqualTo("Test Recipe")
            .jsonPath("$.content[0].ingredients[0].productName").isEqualTo("Flour")

        coVerify { recipeService.findAll(0, 10) }
    }

    @Test
    fun `should return recipes with custom pagination`() {
        // Given
        val pageRequest = PageRequest.of(1, 5)
        val expectedPage = PageImpl<RecipeResponse>(emptyList(), pageRequest, 0)
        coEvery { recipeService.findAll(1, 5) } returns expectedPage

        // When & Then
        webTestClient.get()
            .uri("/recipes?page=1&size=5")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content").isEmpty

        coVerify { recipeService.findAll(1, 5) }
    }

    @Test
    fun `should handle service exceptions`() {
        // Given
        coEvery { recipeService.findAll(any(), any()) } throws RuntimeException("Service error")

        // When & Then
        webTestClient.get()
            .uri("/recipes")
            .exchange()
            .expectStatus().is5xxServerError
    }
}