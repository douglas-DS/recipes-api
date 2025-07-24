package com.ds.recipesapi.dto

data class RecipeResponse(
    val id: Int,
    val name: String,
    val ingredients: List<RecipeIngredientResponse>
)

data class RecipeIngredientResponse(
    val id: Int,
    val productName: String,
    val quantity: Double,
    val unit: String,
)
