package com.ds.recipesapi.service

import com.ds.recipesapi.dto.RecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class RecipeService {
    suspend fun findAll(
        page: Int,
        size: Int
    ): Page<RecipeResponse> = withContext(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

}
