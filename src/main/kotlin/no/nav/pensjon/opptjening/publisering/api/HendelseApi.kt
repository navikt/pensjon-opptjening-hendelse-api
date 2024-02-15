package no.nav.pensjon.opptjening.publisering.api

import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
@Protected
class HendelseApi(
    private val service: HendelseService,
) {

    @PostMapping("/varsel")
    fun varsel(@RequestBody body: String): ResponseEntity<Long> {
        return try {
            ResponseEntity.ok().body(service.handle(body))
        } catch (ex: Exception) {
            log.warn("Exception while handling message", ex)
            ResponseEntity.internalServerError().build()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(HendelseApi::class.java)
    }

}

