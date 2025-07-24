package com.ds.recipesapi.dto

data class RecipeResponse(
    val id: Long,
    val name: String,
    val ingredients: Collection<RecipeIngredientResponse>
)

data class RecipeIngredientResponse(
    val id: Long,
    val name: String,
)
