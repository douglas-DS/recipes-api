package com.ds.recipesapi.controller

import com.ds.recipesapi.dto.RecipeResponse
import com.ds.recipesapi.service.RecipeService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recipes")
class RecipeController(
    private val service: RecipeService
) {
    @GetMapping
    suspend fun getAll(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
    ): Page<RecipeResponse> = service.findAll(page = page, size = size)

}