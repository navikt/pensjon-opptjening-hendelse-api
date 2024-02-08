package no.nav.pensjon.opptjening.publisering.kafka

import no.nav.pensjon.opptjening.publisering.api.Publisher
import no.nav.pensjon.opptjening.publisering.api.Type
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
internal class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${BEHOLDNING_ENDRET_TOPIC}") private val beholdningEndretTopic: String
) : Publisher {
    override fun publish(type: Type, message: String): Long {
        return when (type) {
            Type.BEHOLDNING_ENDRET -> kafkaTemplate.send(beholdningEndretTopic, message)
        }.get().recordMetadata.offset()
    }
}