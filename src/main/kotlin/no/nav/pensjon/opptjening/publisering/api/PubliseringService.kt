package no.nav.pensjon.opptjening.publisering.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Service

@Service
class PubliseringService(
    private val kafkaPublisher: Publisher,
) {
    private val objectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build())

    fun handle(message: String): Long {
        val node = objectMapper.readTree(message)
        val type = Type.valueOf(node.get("type").textValue())
        return kafkaPublisher.publish(type, message)
    }
}