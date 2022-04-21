package no.nav.pensjon.opptjening.pgiendring.api

import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = LoggerFactory.getLogger(PgiEndringApi::class.java)

@RestController()
@RequestMapping("/pgi/")
@Protected
class PgiEndringApi {

    @PostMapping("publiser/endring")
    fun publiserPgiEndring(@RequestBody pgiEndring: PgiEndring): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}

