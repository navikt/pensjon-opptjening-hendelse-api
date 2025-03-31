package no.nav.pensjon.opptjening.hendelse.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.pensjon.opptjening.hendelse.kafka.EndringsType
import no.nav.pensjon.opptjening.hendelse.kafka.Publisher
import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.stereotype.Service

@Service
class HendelseService(
    private val kafkaPublisher: Publisher,
) {
    companion object {
        private val objectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build())
        private val log = PoppLogger(HendelseService::class.java)
    }

    fun handle(hendelser: List<JsonNode>): PublishEventResult {
        if (hendelser.isEmpty()) {
            log.info("Ingen hendelser å processere")
            return PublishEventResult.Ok(emptyList())
        }

        return try {
            val mappedEvents = hendelser.map { h ->
                val typeNode = h.get("type") ?: throw IllegalArgumentException("Missing 'type' field in event")
                val typeValue =
                    typeNode.textValue() ?: throw IllegalArgumentException("'type' field is not a text value")

                EndringsType.valueOf(typeValue) to h.toString()
            }

            kafkaPublisher.publish(mappedEvents).let { offsets ->
                log.info("Offsets prosessert: $offsets")
                PublishEventResult.Ok(offsets)
            }
        } catch (ex: Exception) {
            log.error("Feil ved transaksjonell publisering av hendelser: $hendelser til kafka: $ex")
            PublishEventResult.EventError()
        }
    }
}

sealed class PublishEventResult {
    data class Ok(
        val offsets: List<Long>
    ) : PublishEventResult()

    data class EventError(
        val message: String = "Feil ved transaksjonell publisering av hendelser. Se logger for mer informasjon",
    ) : PublishEventResult()
}
