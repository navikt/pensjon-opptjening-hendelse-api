package no.nav.pensjon.opptjening.hendelse.utils

import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers
import org.slf4j.LoggerFactory


class PoppLogger(clazz: Class<*>) {

    companion object {
        const val PERSON_ID_MARKER = "personId"
        const val EXCEPTION_TYPE_MARKER = "exceptionType"
    }

    private val log = LoggerFactory.getLogger(clazz)

    fun debug(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.debug(marker, msg, ex)
    }

    fun info(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.info(marker, msg, ex)
    }

    fun warn(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.warn(marker, msg, ex)
    }

    fun error(msg: String, ex: Throwable? = null, markers: MarkerProperties? = null) {
        val marker = toMarker(markers, ex)
        log.error(marker, msg, ex)
    }

    private fun toMarker(markers: Map<String, Any?>?, ex: Throwable?): LogstashMarker {
        return markers?.let {
            Markers.appendEntries(markers.plus(EXCEPTION_TYPE_MARKER to (ex?.let { ex -> ex::class.qualifiedName }
                ?: "")))
        } ?: Markers.empty()
    }

    class MarkerProperties(properties: Map<String, Any?>) : Map<String, Any?> by properties
}
