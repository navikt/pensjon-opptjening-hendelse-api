package no.nav.pensjon.opptjening.pgiendring.api

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = LoggerFactory.getLogger(PgiEndringApi::class.java)

@RestController()
@RequestMapping("/pgi/")
class PgiEndringApi {

    @PostMapping("publiser/endring")
    fun publiserPgiEndring(@RequestBody pgiEndring: PgiEndring): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("ping")
    fun ping() {
        logger.info("Test logging !!")
    }
}

data class PgiEndring(
    val aar: Int,
    val fnr: String,
    val opptjeningType: String,
)