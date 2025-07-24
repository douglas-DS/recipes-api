package com.ds.recipesapi.service

import com.ds.recipesapi.dto.AddRecipeResponse
import com.ds.recipesapi.dto.CartResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CartService {

    suspend fun findOne(id: Long): CartResponse = withContext(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    suspend fun addRecipe(id: Long, recipeId: Long): AddRecipeResponse = withContext(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    suspend fun deleteOne(id: Long): Unit = withContext(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

}
