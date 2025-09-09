package no.nav.pensjon.opptjening.hendelse.kafka

import no.nav.pensjon.opptjening.hendelse.api.MottattHendelse
import no.nav.pensjon.opptjening.hendelse.api.PublisertHendelse
import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

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

    override fun publish(hendelser: List<MottattHendelse>): List<PublisertHendelse> {
        return kafkaTemplate.executeInTransaction { template ->
            hendelser
                .map {
                    it to when (it.type) {
                        EndringsType.ENDRET_BEHOLDNING -> template.send(beholdningEndretTopic, it.jsonString)
                        EndringsType.ENDRET_OPPTJENING -> template.send(opptjeningEndretTopic, it.jsonString)
                    }
                }
                .map { (hendelse, metadata) ->
                    PublisertHendelse(hendelse, metadata.get().recordMetadata)
                }
        }
    }
}

enum class EndringsType {
    ENDRET_BEHOLDNING,
    ENDRET_OPPTJENING,
}
