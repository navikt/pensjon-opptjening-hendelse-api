package no.nav.pensjon.opptjening.hendelse.kafka

import no.nav.pensjon.opptjening.hendelse.api.MottattHendelse
import no.nav.pensjon.opptjening.hendelse.api.PublisertHendelse
import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publishes [MottattHendelse] events and returns the resulting [PublisertHendelse]s
 * containing Kafka record metadata.
 */
interface Publisher {
    fun publish(hendelser: List<MottattHendelse>): List<PublisertHendelse>
}

@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    customProducerListener: CustomProducerListener,
    @param:Value("\${BEHOLDNING_ENDRET_TOPIC}") private val beholdningEndretTopic: String,
    @param:Value("\${OPPTJENING_ENDRET_TOPIC}") private val opptjeningEndretTopic: String
) : Publisher {

    companion object {
        private val log = PoppLogger(this::class.java)
    }

    init {
        kafkaTemplate.setProducerListener(customProducerListener)
    }

    override fun publish(hendelser: List<MottattHendelse>): List<PublisertHendelse> =
        kafkaTemplate.executeInTransaction { template ->
            hendelser
                .map { hendelse -> hendelse to template.send(hendelse.topic(), hendelse.jsonString) }
                .map { (hendelse, future) -> PublisertHendelse(hendelse, future.get().recordMetadata) }
        }.orEmpty()

    private fun MottattHendelse.topic(): String = when (type) {
        EndringsType.ENDRET_BEHOLDNING -> beholdningEndretTopic
        EndringsType.ENDRET_OPPTJENING -> opptjeningEndretTopic
    }
}

enum class EndringsType {
    ENDRET_BEHOLDNING,
    ENDRET_OPPTJENING,
}
