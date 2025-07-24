package com.ds.recipesapi.controller

import com.ds.recipesapi.dto.AddRecipeRequest
import com.ds.recipesapi.dto.CartItemKind
import com.ds.recipesapi.dto.CartItemResponse
import com.ds.recipesapi.dto.CartResponse
import com.ds.recipesapi.service.CartService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(CartController::class)
class CartControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var cartService: CartService

    @Test
    fun `should get cart by id`() {
        // Given
        val cartResponse = CartResponse(id = 1, total = 1000, items = emptyList())
        coEvery { cartService.findOne(1) } returns cartResponse

        // When & Then
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)

        coVerify { cartService.findOne(1) }
    }

    @Test
    fun `should add recipe to cart`() {
        // Given
        val request = AddRecipeRequest(recipeId = 2)
        val response = CartResponse(id = 1, total = 1000, items = listOf(CartItemResponse(id = 2, name = "Test", kind = CartItemKind.RECIPE)))
        coEvery { cartService.addRecipe(id = 1, recipeId = 2) } returns response

        // When & Then
        webTestClient.post()
            .uri("/carts/1/add_recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.items").isNotEmpty
            .jsonPath("$.items[0].id").isEqualTo(2)
            .jsonPath("$.items[0].kind").isEqualTo("RECIPE")

        coVerify { cartService.addRecipe(1, 2) }
    }

    @Test
    fun `should delete recipe from cart`() {
        // Given
        coEvery { cartService.removeOneRecipe(id = 1, recipeId = 2) } returns Unit

        // When & Then
        webTestClient.delete()
            .uri("/carts/1/recipes/2")
            .exchange()
            .expectStatus().isNoContent

        coVerify { cartService.removeOneRecipe(id = 1, recipeId = 2) }
    }

    @Test
    fun `should handle service exceptions for get cart`() {
        // Given
        coEvery { cartService.findOne(any()) } throws RuntimeException("Service error")

        // When & Then
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().is5xxServerError
    }
}