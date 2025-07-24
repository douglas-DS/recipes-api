package com.ds.recipesapi.controller

import com.ds.recipesapi.dto.AddRecipeRequest
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
    @Operation(summary = "Returns a cart by its ID")
    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: Int): CartResponse = service.findOne(id = id)

    @Operation(summary = "Adds a recipe to a cart")
    @PostMapping("/{id}/add_recipe")
    suspend fun addRecipe(
        @PathVariable id: Int,
        @RequestBody body: AddRecipeRequest,
    ): CartResponse = service.addRecipe(id = id, recipeId = body.recipeId)

    @Operation(summary = "Removes a specific recipe from a cart")
    @DeleteMapping("/{cartId}/recipes/{recipeId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    suspend fun removeRecipeById(
        @PathVariable cartId: Int,
        @PathVariable recipeId: Int,
    ) {
        service.removeOneRecipe(id = cartId, recipeId = recipeId)
    }

}
