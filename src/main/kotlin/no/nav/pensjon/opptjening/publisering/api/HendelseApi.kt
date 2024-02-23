package no.nav.pensjon.opptjening.publisering.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.security.token.support.core.api.Protected
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
    @PostMapping("/hendelser", consumes = ["application/json"])
    fun hendelser(@RequestBody hendelser: List<JsonNode>): ResponseEntity<List<Long>> {
        return ResponseEntity.ok().body(service.handle(hendelser))
    }
}

