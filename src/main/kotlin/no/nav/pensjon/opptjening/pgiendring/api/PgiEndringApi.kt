package no.nav.pensjon.opptjening.pgiendring.api

import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = LoggerFactory.getLogger(PgiEndringApi::class.java)

@RestController()
@RequestMapping("/pgi/")
@Protected
class PgiEndringApi(registry: MeterRegistry) {
    private val totalCallsToPgiEndring = registry.counter("PgiEndringKall", "antall", "total")

    @PostMapping("publiser/endring")
    fun publiserPgiEndring(@RequestBody pgiEndring: PgiEndring): ResponseEntity<Unit> {
        totalCallsToPgiEndring.increment()
        return ResponseEntity.ok().build()
    }
}

