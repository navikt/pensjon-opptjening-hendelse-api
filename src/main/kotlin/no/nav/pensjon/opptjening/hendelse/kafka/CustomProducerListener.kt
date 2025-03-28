package no.nav.pensjon.opptjening.hendelse.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.ProducerListener
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CustomProducerListener : ProducerListener<String, String> {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        log.info("Custom producer listener initialized")
    }

    override fun onSuccess(producerRecord: ProducerRecord<String, String>?, recordMetadata: RecordMetadata?) {
        log.info(
            "Kafka message published successfully - Topic: {}, Partition: {}, Offset: {}, Timestamp: {}, Key: {}",
            producerRecord?.topic() ?: "unknown",
            producerRecord?.partition() ?: -1,
            recordMetadata?.offset() ?: -1,
            LocalDateTime.now(),
            producerRecord?.key() ?: "null"
        )
    }

    override fun onError(
        producerRecord: ProducerRecord<String, String>?,
        @Nullable recordMetadata: RecordMetadata?,
        exception: Exception?
    ) {
        log.error(
            "Kafka message publishing failed - Topic: {}, Partition: {}, Key: {}",
            producerRecord?.topic() ?: "unknown",
            producerRecord?.partition() ?: -1,
            producerRecord?.key() ?: "null",
            exception
        )
        log.error("Failed message payload: {}", producerRecord?.value())
    }
}