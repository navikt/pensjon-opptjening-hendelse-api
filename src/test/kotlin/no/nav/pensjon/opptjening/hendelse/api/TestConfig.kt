package no.nav.pensjon.opptjening.hendelse.api

import no.nav.pensjon.opptjening.hendelse.kafka.CustomProducerListener
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.ProducerListener

@TestConfiguration
class TestConfig {

    @Bean
    fun customProducerListener(): CustomProducerListener = CustomProducerListener()

    @Bean
    fun producerListener(customProducerListener: CustomProducerListener): ProducerListener<String, String> =
        customProducerListener

    @Bean
    fun kafkaTemplate(
        @Value("\${spring.kafka.bootstrap-servers:localhost:9092}") bootstrapServers: String
    ): KafkaTemplate<String, String> {
        val producerProps = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        )
        return KafkaTemplate(DefaultKafkaProducerFactory(producerProps))
    }

    @Bean
    @Qualifier("beholdningEndretTopic")
    fun beholdningEndretTopic(
        @Value("\${kafka.topics.beholdning-endret:beholdning-endret-topic}") topic: String
    ): String = topic

    @Bean
    @Qualifier("opptjeningEndretTopic")
    fun opptjeningEndretTopic(
        @Value("\${kafka.topics.opptjening-endret:opptjening-endret-topic}") topic: String
    ): String = topic
}