package com.ds.recipesapi.dto

data class CartResponse(
    val id: Int,
    val total: Long,
    val items: List<CartItemResponse>
)

data class CartItemResponse(
    val id: Int,
    val name: String,
    val kind: CartItemKind,
    val total: Long,
)

enum class CartItemKind {
    PRODUCT, RECIPE;
}
