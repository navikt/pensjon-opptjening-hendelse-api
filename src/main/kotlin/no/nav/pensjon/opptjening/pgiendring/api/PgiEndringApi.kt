package no.nav.pensjon.opptjening.pgiendring.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


private val logger = LoggerFactory.getLogger(PgiEndringApi::class.java)

@RestController
@RequestMapping("/pgi/")
@Protected
class PgiEndringApi(registry: MeterRegistry, private val kafkaTemplate: KafkaTemplate<String, String>) {
    private val counterTotalCalls = registry.counter("PgiEndringKall", "antall", "total")
    private val counterFailedCalls = registry.counter("PgiEndringKall", "antall", "feilet")
    private val counterSuccessfulCalls = registry.counter("PgiEndringKall", "antall", "suksess")

    private val objectMapper = ObjectMapper()

    @PostMapping("publiser/endring")
    fun publiserPgiEndring(@RequestBody pgiEndring: PgiEndring): ResponseEntity<String> {
        counterTotalCalls.increment()

        return try {
            kafkaTemplate.sendDefault(createKey(pgiEndring).toJson(), pgiEndring.toJson()).get()
            counterSuccessfulCalls.increment()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error("Something went wrong when adding pgiEndring to topic ", e)
            counterFailedCalls.increment()
            ResponseEntity.internalServerError().body("""{ "error": "Something went wrong when adding pgiEndring to topic"}""");
        }
    }

    private fun Any.toJson() = objectMapper.writeValueAsString(this)

}

