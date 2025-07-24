package com.ds.recipesapi.service

import com.ds.recipesapi.dto.CartItemKind
import com.ds.recipesapi.dto.CartItemResponse
import com.ds.recipesapi.dto.CartResponse
import com.ds.recipesapi.entity.Cart
import com.ds.recipesapi.entity.CartItem
import com.ds.recipesapi.error.HttpException
import com.ds.recipesapi.repository.CartRepository
import com.ds.recipesapi.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepo: CartRepository,
    private val recipeRepo: RecipeRepository
) {
    suspend fun findOne(id: Int): CartResponse = findById(id = id).toResponse()

    @Transactional
    suspend fun addRecipe(
        id: Int,
        recipeId: Int
    ): CartResponse = withContext(Dispatchers.IO) {
        val recipe = recipeRepo.findById(recipeId)
            .orElseThrow { throw HttpException.NotFound("Recipe with id $recipeId not found") }

        val cart = findById(id = id)

        cart.addRecipe(recipe = recipe)
            .run { cartRepo.save(cart) }
            .toResponse()
    }

    @Transactional
    suspend fun removeOneRecipe(
        id: Int,
        recipeId: Int
    ): Unit = withContext(Dispatchers.IO) {
        val cart = findById(id = id)
        val item = cart.items.find { item -> item.recipe?.id == recipeId }
            ?: throw HttpException.NotFound("Cart item with recipe id $recipeId not found")
        cart.items.remove(item)
        cartRepo.save(cart)
    }

    private suspend fun findById(id: Int): Cart = withContext(Dispatchers.IO) {
        cartRepo.findById(id)
            .orElseThrow {
                throw HttpException.NotFound("Cart with id $id not found")
            }
    }

    private fun Cart.toResponse(): CartResponse = CartResponse(
        id = id ?: throw HttpException.NotFound("Cart id not found"),
        total = totalInCents,
        items = items.map { item -> item.toResponse() }
    )

    private fun CartItem.toResponse(): CartItemResponse = CartItemResponse(
        id = id ?: throw HttpException.NotFound("Cart item id not found"),
        name = product?.name ?: recipe?.name ?: throw HttpException.NotFound("Product or recipe not found"),
        kind = when {
            product != null -> CartItemKind.PRODUCT
            recipe != null -> CartItemKind.RECIPE
            else -> throw HttpException.NotFound("Product or recipe not found")
        }
    )
}
