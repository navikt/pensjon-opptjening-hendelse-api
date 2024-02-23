package no.nav.pensjon.opptjening.hendelse.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val FNR_REGEX = "(\\d{6})\\d{5}".toRegex()
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    data class ExceptionBody(val message: String)

    @ExceptionHandler(value = [PublishFailedException::class])
    fun handle(ex: PublishFailedException, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return handleExceptionInternal(
            ex,
            ExceptionBody(ex.message ?: ""),
            HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        )
    }

    @ExceptionHandler(value = [Exception::class])
    fun logAndHandleException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return handleException(ex, request)
    }

    private fun logExeption(ex: Exception) {
        log.warn(
            "{}, {}",
            ex.let { "Exception: ${it::class.java.simpleName}, message: ${it.message?.removeFnr()}, location: ${it.stackTrace.first().className} ${it.stackTrace.first().lineNumber}" },
            ex.cause?.let { "Cause: ${it::class.java.simpleName}, message: ${it.message?.removeFnr()}" })
    }

    private fun String.removeFnr(): String = FNR_REGEX.replace(this, "\$1*****")

}