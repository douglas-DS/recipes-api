package com.ds.recipesapi.service

import com.ds.recipesapi.dto.RecipeIngredientResponse
import com.ds.recipesapi.dto.RecipeResponse
import com.ds.recipesapi.entity.Recipe
import com.ds.recipesapi.entity.RecipeIngredient
import com.ds.recipesapi.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val repo: RecipeRepository
) {
    suspend fun findAll(
        page: Int,
        size: Int
    ): Page<RecipeResponse> = withContext(Dispatchers.IO) {
        repo
            .findAll(PageRequest.of(page, size))
            .map { recipe -> recipe.toResponse() }
    }

    private fun Recipe.toResponse(): RecipeResponse = RecipeResponse(
        id = id!!,
        name = name,
        ingredients = ingredients.map { ingredient -> ingredient.toResponse() }
    )

    private fun RecipeIngredient.toResponse(): RecipeIngredientResponse =
        RecipeIngredientResponse(
            id = id!!,
            productName = product.name,
            quantity = quantity,
            unit = unit,
        )

}
