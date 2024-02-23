package no.nav.pensjon.opptjening.hendelse.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Service

@Service
class HendelseService(
    private val kafkaPublisher: Publisher,
) {
    private val objectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build())

    fun handle(hendelser: List<JsonNode>): List<Long> {
        return kafkaPublisher.publish(
            hendelser.map { h ->
                Type.valueOf(h.get("type").textValue()) to h.toString()
            }
        )
    }
}