package com.ds.recipesapi.controller

import com.ds.recipesapi.dto.AddRecipeRequest
import com.ds.recipesapi.dto.AddRecipeResponse
import com.ds.recipesapi.dto.CartResponse
import com.ds.recipesapi.service.CartService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/carts")
class CartController(
    private val service: CartService,
) {
    @Operation(summary = "Get cart by id")
    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: Long): CartResponse = service.findOne(id = id)

    @Operation(summary = "Add recipe to cart")
    @PostMapping("/{id}/add_recipe")
    suspend fun addRecipe(
        @PathVariable id: Long,
        @RequestBody requestBody: AddRecipeRequest,
    ): AddRecipeResponse = service.addRecipe(id = id, recipeId = requestBody.recipeId)

    @Operation(summary = "Delete recipe from a specific cart")
    @DeleteMapping("/{cartId}/recipes/{recipeId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    suspend fun deleteById(
        @PathVariable cartId: Long,
        @PathVariable recipeId: Long,
    ) {
        service.deleteOne(id = cartId)
    }

}
