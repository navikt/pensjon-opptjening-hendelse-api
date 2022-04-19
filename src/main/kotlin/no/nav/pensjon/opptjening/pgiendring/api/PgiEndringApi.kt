package no.nav.pensjon.opptjening.pgiendring.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = LoggerFactory.getLogger(PgiEndringApi::class.java)

@RestController()
@RequestMapping("/pgi/")
class PgiEndringApi {

    @PostMapping("publiser/endring")
    fun publiserPgiEndring() {

    }

    @GetMapping("ping")
    fun ping() {
        logger.info("Test logging !!")
    }


}