package no.nav.pensjon.opptjening.publisering.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Service

@Service
class HendelseService(
    private val kafkaPublisher: Publisher,
) {
    private val objectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build())

    fun handle(hendelser: List<String>): List<Long> {
        return kafkaPublisher.publish(
            hendelser.map { h ->
                objectMapper.readTree(h).let { Type.valueOf(it.get("type").textValue()) } to h
            }
        )
    }
}