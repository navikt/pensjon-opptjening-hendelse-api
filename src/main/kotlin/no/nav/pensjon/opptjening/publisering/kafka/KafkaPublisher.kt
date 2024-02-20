package no.nav.pensjon.opptjening.publisering.kafka

import no.nav.pensjon.opptjening.publisering.api.PublishFailedException
import no.nav.pensjon.opptjening.publisering.api.Publisher
import no.nav.pensjon.opptjening.publisering.api.Type
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
internal class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${BEHOLDNING_ENDRET_TOPIC}") private val beholdningEndretTopic: String
) : Publisher {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun publish(hendelser: List<Pair<Type, String>>): List<Long> {
        return try {
            kafkaTemplate.executeInTransaction { template ->
                hendelser
                    .map { (type, hendelse) ->
                        when (type) {
                            Type.BEHOLDNING_ENDRET -> template.send(beholdningEndretTopic, hendelse)
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