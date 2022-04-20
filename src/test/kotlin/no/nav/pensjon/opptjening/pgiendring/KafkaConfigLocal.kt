package no.nav.pensjon.opptjening.pgiendring

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Profile("local")
@EnableKafka
@Configuration
class KafkaConfigLocal(
    @Value("\${kafka.brokers}") private val localBootstrapServers: String,
    @Value("\${KAFKA_PGI_ENDRING_TOPIC}") private val pgiEndringTopic: String,
) {

    @Bean
    fun aivenKafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(localProducerFactory()).also { it.defaultTopic = pgiEndringTopic }

    @Bean
    fun localProducerFactory(): ProducerFactory<String, String> = DefaultKafkaProducerFactory(aivenProducerConfig() + localSecurityConfig())

    private fun aivenProducerConfig() = mapOf(
        ProducerConfig.CLIENT_ID_CONFIG to "pgi-endring",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to localBootstrapServers,
    )

    private fun localSecurityConfig() = mapOf(
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SASL_SSL",
        SaslConfigs.SASL_MECHANISM to "PLAIN"
    )
}