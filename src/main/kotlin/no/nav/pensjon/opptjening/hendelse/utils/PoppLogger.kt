package no.nav.pensjon.opptjening.hendelse.utils

import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class PoppLogger(clazz: Class<*>) {

    companion object {
        const val PERSON_ID_MARKER = "personId"
        const val EXCEPTION_TYPE_MARKER = "exceptionType"
    }

    private val log = LoggerFactory.getLogger(clazz)

    fun debug(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.debug(marker, maskFnr(msg))
//        SecureLog.debug(marker, msg, ex)
    }

    fun info(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.info(marker, maskFnr(msg))
//        SecureLog.info(marker, msg, ex)
    }

    fun warn(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.warn(marker, maskFnr(msg))
//        SecureLog.warn(marker, msg, ex)
    }

    fun error(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.error(marker, maskFnr(msg))
//        SecureLog.error(marker, msg, ex)
    }

    private fun toMarker(markers: Map<String, Any?>?, ex: Throwable?): LogstashMarker {
        return markers?.let {
            Markers.appendEntries(markers.plus(EXCEPTION_TYPE_MARKER to (ex?.let { ex -> ex::class.qualifiedName }
                ?: "")))
        } ?: Markers.empty()
    }

    class MarkerProperties(properties: Map<String, Any?>) : Map<String, Any?> by properties
}

//private val logger: Logger = LoggerFactory.getLogger("secure")

//object SecureLog : Logger by logger

private val fnrRegex = "(\\d{6})\\d{5}".toRegex()
fun maskFnr(text: String?) = text?.let { fnrRegex.replace(it, "\$1*****") }