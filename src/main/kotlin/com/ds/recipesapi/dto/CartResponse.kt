package com.ds.recipesapi.dto

data class CartResponse(
    val id: Long,
    val items: Collection<CartItemResponse>,
    val total: Double
)

data class CartItemResponse(
    val id: Long,
    val name: String,
)