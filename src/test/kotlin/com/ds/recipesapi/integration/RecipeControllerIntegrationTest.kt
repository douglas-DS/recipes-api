package com.ds.recipesapi.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RecipeControllerIntegrationTest(
    @Autowired private val webTestClient: WebTestClient
) : BaseIntegrationTest() {

    @Test
    fun `should return all recipes with default pagination`() {
        webTestClient.get()
            .uri("/recipes")
            .exchange()
            .expectStatus().isOk
            .expectBody<Map<String, Any>>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(7, body["totalElements"])
                assertEquals(0, body["number"])
                assertEquals(10, body["size"])
                assertFalse(body["empty"] as Boolean)

                val content = body["content"] as List<*>
                assertEquals(7, content.size)

                // Verify first recipe
                val firstRecipe = content[0] as Map<*, *>
                assertEquals(1, firstRecipe["id"])
                assertEquals("Classic Chocolate Chip Cookies", firstRecipe["name"])
            }
    }

    @Test
    fun `should return recipes with custom pagination`() {
        webTestClient.get()
            .uri("/recipes?page=0&size=3")
            .exchange()
            .expectStatus().isOk
            .expectBody<Map<String, Any>>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(7, body["totalElements"])
                assertEquals(0, body["number"])
                assertEquals(3, body["size"])
                assertEquals(3, body["numberOfElements"])

                val content = body["content"] as List<*>
                assertEquals(3, content.size)
            }
    }

    @Test
    fun `should return second page of recipes`() {
        webTestClient.get()
            .uri("/recipes?page=1&size=3")
            .exchange()
            .expectStatus().isOk
            .expectBody<Map<String, Any>>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(7, body["totalElements"])
                assertEquals(1, body["number"])
                assertEquals(3, body["size"])
                assertEquals(3, body["numberOfElements"])
            }
    }

    @Test
    fun `should return empty page when page number exceeds available data`() {
        webTestClient.get()
            .uri("/recipes?page=10&size=5")
            .exchange()
            .expectStatus().isOk
            .expectBody<Map<String, Any>>()
            .consumeWith { response ->
                val body = response.responseBody!!
                assertEquals(7, body["totalElements"])
                assertEquals(10, body["number"])
                assertEquals(5, body["size"])
                assertEquals(0, body["numberOfElements"])
                assertTrue(body["empty"] as Boolean)
            }
    }
}