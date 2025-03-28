package no.nav.pensjon.opptjening.hendelse.api

import no.nav.pensjon.opptjening.hendelse.kafka.CustomProducerListener
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.ProducerListener
import org.apache.kafka.common.serialization.StringSerializer

@TestConfiguration
class TestKafkaConfig {
    @Bean
    fun customProducerListener(): CustomProducerListener {
        return CustomProducerListener()
    }

    @Bean
    fun producerListener(customProducerListener: CustomProducerListener): ProducerListener<String, String> {
        return customProducerListener
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        val producerProps = HashMap<String, Any>()
        producerProps["bootstrap.servers"] = "localhost:9092"
        producerProps["key.serializer"] = StringSerializer::class.java
        producerProps["value.serializer"] = StringSerializer::class.java

        val producerFactory = DefaultKafkaProducerFactory<String, String>(producerProps)
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun beholdningEndretTopic(): String = "beholdning-endret-topic"

    @Bean
    fun opptjeningEndretTopic(): String = "opptjening-endret-topic"
}