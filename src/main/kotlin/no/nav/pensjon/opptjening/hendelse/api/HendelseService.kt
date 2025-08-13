package no.nav.pensjon.opptjening.hendelse.api

import com.fasterxml.jackson.databind.JsonNode
import no.nav.pensjon.opptjening.hendelse.kafka.Publisher
import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.stereotype.Service

@Service
class HendelseService(
    private val kafkaPublisher: Publisher,
) {
    companion object {
        private val log = PoppLogger(HendelseService::class.java)
    }

    fun handle(hendelser: List<JsonNode>): PublishEventResult {
        if (hendelser.isEmpty()) {
            log.info("Ingen hendelser å processere")
            return PublishEventResult.Ok(emptyList())
        }

        return try {
            hendelser
                .map { MottattHendelse(it) }
                .let { mottatt ->
                    kafkaPublisher.publish(mottatt).let { publisert ->
                        log.info("Publisert kafka: $publisert")
                        PublishEventResult.Ok(publisert.map { it.hendelse.id })
                    }
                }
        } catch (ex: Exception) {
            log.error("Feil ved transaksjonell publisering av hendelser: $hendelser til kafka med exception: $ex")
            PublishEventResult.EventError()
        }
    }
}

sealed class PublishEventResult {
    data class Ok(
        val hendelser: List<String>
    ) : PublishEventResult()

    data class EventError(
        val message: String = "Feil ved transaksjonell publisering av hendelser. Se logger for mer informasjon",
    ) : PublishEventResult()
}
