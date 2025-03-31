package no.nav.pensjon.opptjening.hendelse.kafka

import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.kafka.support.ProducerListener
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomProducerListener : ProducerListener<String, String> {

    companion object {
        private val log = PoppLogger(this::class.java)
    }

    init {
        log.info("Custom producer listener initialized")
    }

    override fun onSuccess(producerRecord: ProducerRecord<String, String>?, recordMetadata: RecordMetadata?) {
        log.info(
            "Kafka message published successfully - " +
                    "Topic: ${producerRecord?.topic() ?: "unknown"}, " +
                    "Partition: ${producerRecord?.partition() ?: "unknown"}, " +
                    "Offset: ${recordMetadata?.offset() ?: "unknown"}, " +
                    "Timestamp: ${LocalDateTime.now()}, " +
                    "Key: ${producerRecord?.key() ?: "unknown"}"
        )
    }

    override fun onError(
        producerRecord: ProducerRecord<String, String>?,
        @Nullable recordMetadata: RecordMetadata?,
        exception: Exception?
    ) {
        log.error(
            "Kafka message publishing failed - " +
                    "Topic: ${producerRecord?.topic() ?: "unknown"}, " +
                    "Partition: ${producerRecord?.partition() ?: "unknown"}, " +
                    "Offset: ${recordMetadata?.offset() ?: "unknown"}, " +
                    "Timestamp: ${LocalDateTime.now()}, " +
                    "Key: ${producerRecord?.key() ?: "unknown"}" +
                    "Payload: ${producerRecord?.value() ?: "unknown"}" +
                    "Exception: ${exception?.message ?: "unknown"}"
        )
    }
}