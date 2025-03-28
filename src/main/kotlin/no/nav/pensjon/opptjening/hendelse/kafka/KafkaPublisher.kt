package no.nav.pensjon.opptjening.hendelse.kafka

import no.nav.pensjon.opptjening.hendelse.api.PublishFailedException
import no.nav.pensjon.opptjening.hendelse.api.Publisher
import no.nav.pensjon.opptjening.hendelse.api.Type
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val customProducerListener: CustomProducerListener,
    @Value("\${BEHOLDNING_ENDRET_TOPIC}") private val beholdningEndretTopic: String,
    @Value("\${OPPTJENING_ENDRET_TOPIC}") private val opptjeningEndretTopic: String
) : Publisher {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        kafkaTemplate.setProducerListener(customProducerListener)
    }

    override fun publish(hendelser: List<Pair<Type, String>>): List<Long> {
        return try {
            kafkaTemplate.executeInTransaction { template ->
                hendelser
                    .map { (type, hendelse) ->
                        when (type) {
                            Type.ENDRET_BEHOLDNING -> template.send(beholdningEndretTopic, hendelse)
                            Type.ENDRET_OPPTJENING -> template.send(opptjeningEndretTopic, hendelse)
                        }
                    }
                    .map {
                        it.get().recordMetadata.offset()
                    }
            }
        } catch (ex: Exception) {
            throw PublishFailedException("Feil ved transaksjonell publisering av meldinger til kafka", ex)
        }
    }
}