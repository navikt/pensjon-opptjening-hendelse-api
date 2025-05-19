package no.nav.pensjon.opptjening.hendelse.utils

import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

class PoppLogger(clazz: Class<*>) {

    private val log = LoggerFactory.getLogger(clazz)
    private val teamLogsMarker = MarkerFactory.getMarker("TEAM_LOGS")

    fun debug(msg: String, ex: Throwable? = null) {
        log.debug(maskFnr(msg))
        log.debug(teamLogsMarker, msg, ex)
    }

    fun info(msg: String, ex: Throwable? = null) {
        log.info(maskFnr(msg))
        log.info(teamLogsMarker, msg, ex)
    }

    fun warn(msg: String, ex: Throwable? = null) {
        log.warn(maskFnr(msg))
        log.warn(teamLogsMarker, msg, ex)
    }

    fun error(msg: String, ex: Throwable? = null) {
        log.error(maskFnr(msg))
        log.error(teamLogsMarker, msg, ex)
    }

    private val fnrRegex = "(\\d{6})\\d{5}".toRegex()
    private fun maskFnr(text: String?) = text?.let { fnrRegex.replace(it, "\$1*****") }
}
