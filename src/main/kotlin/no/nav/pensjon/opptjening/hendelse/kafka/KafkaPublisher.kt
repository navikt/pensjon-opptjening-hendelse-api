package no.nav.pensjon.opptjening.hendelse.kafka

import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

interface Publisher {
    fun publish(hendelser: List<Pair<EndringsType, String>>): List<Long>
}

@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    customProducerListener: CustomProducerListener,
    @Value("\${BEHOLDNING_ENDRET_TOPIC}") private val beholdningEndretTopic: String,
    @Value("\${OPPTJENING_ENDRET_TOPIC}") private val opptjeningEndretTopic: String
) : Publisher {

    companion object {
        private val log = PoppLogger(this::class.java)
    }

    init {
        kafkaTemplate.setProducerListener(customProducerListener)
    }

    override fun publish(hendelser: List<Pair<EndringsType, String>>): List<Long> {
        return kafkaTemplate.executeInTransaction { template ->
            hendelser
                .map { (type, hendelse) ->
                    when (type) {
                        EndringsType.ENDRET_BEHOLDNING -> template.send(beholdningEndretTopic, hendelse)
                        EndringsType.ENDRET_OPPTJENING -> template.send(opptjeningEndretTopic, hendelse)
                    }
                }
                .map {
                    it.get().recordMetadata.offset()
                }
        }
    }
}

enum class EndringsType {
    ENDRET_BEHOLDNING,
    ENDRET_OPPTJENING,
}
