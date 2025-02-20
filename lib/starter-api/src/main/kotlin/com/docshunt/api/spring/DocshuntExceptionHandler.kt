package com.docshunt.api.spring

import com.docshunt.api.ApiResult
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@EnableConfigurationProperties(DocshuntExceptionHandler.Properties::class)
class DocshuntExceptionHandler(val properties: Properties) {

    @ConfigurationProperties(prefix = "be.api.error")
    data class Properties(
        val includeException: Boolean = false,
        val includeStackTrace: Boolean = false,
        val filterStackTrace: Set<String> = setOf(".docshunt.")
    )

    private val logger = KotlinLogging.logger {}

    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(Throwable::class)
    fun handle(e: Throwable, request: HttpServletRequest): ResponseEntity<ApiResult<Unit>> {
        logger.error(e) { "${request.method} ${request.requestURL}" }
        return ResponseEntity.internalServerError().body(ApiResult(Unit, error("internal server error", e)))
    }


    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(HttpException::class)
    fun handle(e: HttpException, request: HttpServletRequest): ResponseEntity<ApiResult<Unit>> {
        when (e.status.is4xxClientError) {
            true -> logger.warn(e) { "${e.filterStackTrace().status} ${request.method} ${request.requestURL}" }
            false -> logger.error(e) { "${e.filterStackTrace().status} ${request.method} ${request.requestURL}" }
        }
        val error = error(e.responseMessage, if (properties.includeStackTrace) e.filterStackTrace() else e, e.errorCode)
        return ResponseEntity.status(e.status).body(ApiResult(Unit, error))
    }

    private fun HttpException.filterStackTrace() = apply {
        stackTrace = stackTrace.filter { e -> properties.filterStackTrace.any { it in e.className } }.toTypedArray()
    }

    private fun error(message: String, e: Throwable, errorCode: String? = null) = ApiResult.Error(
        message = message,
        code = errorCode,
        exception = e.message.takeIf { properties.includeException },
        stacktrace = e.stackTrace.takeIf { properties.includeStackTrace }?.map { it.toString() }
    )
}
