package com.ds.recipesapi.error

import org.springframework.http.HttpStatus

sealed class HttpException(
    val status: HttpStatus,
    message: String,
    cause: Throwable?,
) : RuntimeException(message, cause) {
    class BadRequest(message: String, cause: Throwable? = null): HttpException(HttpStatus.BAD_REQUEST, message, cause)
    class Conflict(message: String, cause: Throwable? = null): HttpException(HttpStatus.CONFLICT, message, cause)
    class NotFound(message: String, cause: Throwable? = null): HttpException(HttpStatus.NOT_FOUND, message, cause)
    class Unauthorized(message: String, cause: Throwable? = null): HttpException(HttpStatus.UNAUTHORIZED, message, cause)
    class Forbidden(message: String, cause: Throwable? = null): HttpException(HttpStatus.FORBIDDEN, message, cause)
    class InternalServerError(message: String, cause: Throwable? = null): HttpException(HttpStatus.INTERNAL_SERVER_ERROR, message, cause)
}
