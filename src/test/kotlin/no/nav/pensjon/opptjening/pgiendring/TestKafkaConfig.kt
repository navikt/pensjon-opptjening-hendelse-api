package no.nav.pensjon.opptjening.pgiendring

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.test.EmbeddedKafkaBroker

@Profile("local")
@Configuration
class TestKafkaConfig {
    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>, @Value("\${KAFKA_PGI_ENDRING_TOPIC}") pgiEndringTopic: String): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory).also { it.defaultTopic = pgiEndringTopic }
    }

    @Bean
    fun producerFactory(@Value("\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}") brokers: String): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(mapOf(
            ProducerConfig.CLIENT_ID_CONFIG to "pgi-endring",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to brokers
        ))


    @Bean
    fun consumerFactory(@Value("\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}") brokers: String): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to brokers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.GROUP_ID_CONFIG to "pensjonopptjening",
        ))
    }

    @Bean("testConsumer")
    fun consumerContainerFactory(consumerFactory: ConsumerFactory<String, String>): ConcurrentKafkaListenerContainerFactory<String, String>? =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            this.consumerFactory = consumerFactory
            this.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        }

}