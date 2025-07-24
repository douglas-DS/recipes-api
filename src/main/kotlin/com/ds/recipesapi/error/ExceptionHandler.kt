package com.ds.recipesapi.error

import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionHandler{
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(HttpException::class)
    fun handle(
        exchange: ServerWebExchange,
        ex: HttpException
    ): ResponseEntity<*> = buildExceptionResponse(
        exchange = exchange,
        ex = ex,
        httpStatus = ex.status,
    )

    @ExceptionHandler(WebExchangeBindException::class)
    fun handle(
        exchange: ServerWebExchange,
        ex: WebExchangeBindException
    ): ResponseEntity<*> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val message = ex.bindingResult
            .fieldErrors
            .firstOrNull()
            ?.defaultMessage
            ?: httpStatus.reasonPhrase
        return buildExceptionResponse(
            exchange = exchange,
            ex = ex,
            httpStatus = httpStatus,
            message = message
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(
        exchange: ServerWebExchange,
        ex: ConstraintViolationException
    ): ResponseEntity<*> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val message = ex.constraintViolations
            .firstOrNull()
            ?.message
            ?: httpStatus.reasonPhrase
        return buildExceptionResponse(
            exchange = exchange,
            ex = ex,
            httpStatus = httpStatus,
            message = message,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handle(exchange: ServerWebExchange, ex: Exception): ResponseEntity<*> {
        log.error("Handling general exception", ex)
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val errorMessage = httpStatus.reasonPhrase
        return buildExceptionResponse(
            exchange = exchange,
            ex = ex,
            httpStatus = httpStatus,
            message = errorMessage
        )
    }

    private fun buildExceptionResponse(
        exchange: ServerWebExchange,
        ex: Exception,
        httpStatus: HttpStatus,
        message: String? = null,
    ): ResponseEntity<ResponseObject> {
        val responseMessage = message ?: ex.message ?: ex.localizedMessage ?: ex.cause?.message ?: httpStatus.reasonPhrase
        val response = ResponseObject(
            path = exchange.request.path.toString(),
            requestId = exchange.request.id,
            status = httpStatus.value(),
            message = responseMessage,
            timestamp = OffsetDateTime.now(ZoneOffset.UTC),
        )
        return ResponseEntity(response, httpStatus)
    }

}
