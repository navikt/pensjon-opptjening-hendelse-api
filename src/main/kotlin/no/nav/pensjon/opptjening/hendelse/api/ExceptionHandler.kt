package no.nav.pensjon.opptjening.hendelse.api

import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = PoppLogger(this::class.java)

    @ExceptionHandler(value = [Exception::class])
    fun genericLogExceptionHandler(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        log.warn(
            msg = "Exception: ${ex::class.java.simpleName}, message: ${ex.message}, location: ${ex.stackTrace.first().className} ${ex.stackTrace.first().lineNumber}" +
                    (ex.cause?.let { ", Cause: ${it::class.java.simpleName}, message: ${it.message}" } ?: ""),
            ex = ex)
        return handleException(ex, request)
    }
}