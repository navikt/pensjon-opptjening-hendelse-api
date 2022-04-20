package no.nav.pensjon.opptjening.pgiendring.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

data class PgiEndring(
    val aar: Int,
    val fnr: String,
    val opptjeningType: String,
) {
    init {
        if (aar == 0) throw InntektAarNullException()
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "aar is null")
class InntektAarNullException : Exception()