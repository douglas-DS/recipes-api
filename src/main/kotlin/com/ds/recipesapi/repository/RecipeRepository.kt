package com.ds.recipesapi.repository

import com.ds.recipesapi.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, Int>