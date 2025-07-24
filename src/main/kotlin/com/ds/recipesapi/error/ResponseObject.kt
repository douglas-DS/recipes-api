package com.ds.recipesapi.error

import java.time.OffsetDateTime

data class ResponseObject(
    val timestamp: OffsetDateTime,
    val path: String,
    val status: Int,
    val message: String,
    val requestId: String,
)
