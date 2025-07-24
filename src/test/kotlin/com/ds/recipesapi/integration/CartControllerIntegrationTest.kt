package com.ds.recipesapi.integration

import com.ds.recipesapi.dto.AddRecipeRequest
import com.ds.recipesapi.dto.CartResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CartControllerIntegrationTest(
    @Autowired private val webTestClient: WebTestClient
) : BaseIntegrationTest() {

    @Test
    fun `should get cart by id`() {
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(1, body.id)
                assertEquals(4730, body.total)
                assertEquals(3, body.items.size)
            }
    }

    @Test
    fun `should get empty cart`() {
        webTestClient.get()
            .uri("/carts/4")
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(4, body.id)
                assertEquals(0, body.total)
                assertTrue(body.items.isEmpty())
            }
    }

    @Test
    fun `should return 404 for non-existent cart`() {
        webTestClient.get()
            .uri("/carts/999")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should add recipe to cart`() {
        val requestBody = mapOf("recipeId" to 5)
        
        webTestClient.post()
            .uri("/carts/4/add_recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(4, body.id)
                assertTrue(body.total > 0)
                
                val items = body.items
                assertEquals(1, items.size)

                val recipe = items.first()
                assertEquals(15, recipe.id)
                assertEquals("Herb Butter", recipe.name)
            }
    }

    @Test
    fun `should add recipe to cart and delete`() {
        // Verify initial state
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(1, body.id)
                assertEquals(4730, body.total)
                assertFalse(body.items.isEmpty())
                assertEquals(3, body.items.size)
            }

        val addRecipeRequest = AddRecipeRequest(recipeId = 2)

        // Adds a recipe to the cart
        webTestClient.post()
            .uri("/carts/1/add_recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(addRecipeRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                val items = body.items
                assertEquals(1, body.id)
                assertEquals(6960, body.total)
                assertEquals(4, items.size)

                assertTrue(items.any { item -> item.id == 15 })
                assertTrue(items.any { item -> item.name == "Margherita Pizza" })
            }

        // Deletes the recipe
        webTestClient.delete()
            .uri("/carts/1/recipes/${addRecipeRequest.recipeId}")
            .exchange()
            .expectStatus().isNoContent

        // Verify again the cart status
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(1, body.id)
                assertEquals(4730, body.total)
                assertFalse(body.items.isEmpty())
                assertEquals(3, body.items.size)
            }
    }

    @Test
    fun `should return 404 when adding non-existent recipe to cart`() {
        webTestClient.post()
            .uri("/carts/1/add_recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(AddRecipeRequest(recipeId = 999))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return 404 when adding recipe to non-existent cart`() {
        webTestClient.post()
            .uri("/carts/999/add_recipe")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(AddRecipeRequest(recipeId = 1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should remove recipe from cart`() {
        webTestClient.delete()
            .uri("/carts/1/recipes/1")
            .exchange()
            .expectStatus().isNoContent
        
        // Verify the recipe was removed
        webTestClient.get()
            .uri("/carts/1")
            .exchange()
            .expectStatus().isOk
            .expectBody<CartResponse>()
            .consumeWith { response ->
                val body = response.responseBody!!
                val items = body.items
                assertEquals(2, items.size) // Should now have 2 items instead of 3

                // Verify recipe 1 is no longer in the cart
                assertTrue(items.none { item -> item.id == 1 })
            }
    }

    @Test
    fun `should return 404 when removing non-existent recipe from cart`() {
        webTestClient.delete()
            .uri("/carts/1/recipes/999")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return 404 when removing recipe from non-existent cart`() {
        webTestClient.delete()
            .uri("/carts/999/recipes/1")
            .exchange()
            .expectStatus().isNotFound
    }
}