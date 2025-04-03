package no.nav.pensjon.opptjening.hendelse.kafka

import jakarta.annotation.PreDestroy
import no.nav.pensjon.opptjening.hendelse.utils.PoppLogger
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.UUID

@EnableKafka
@Profile("dev-gcp", "prod-gcp")
@Configuration
class KafkaConfig(
    @Value("\${kafka.keystore.path}") private val keystorePath: String,
    @Value("\${kafka.credstore.password}") private val credstorePassword: String,
    @Value("\${kafka.truststore.path}") private val truststorePath: String,
    @Value("\${kafka.brokers}") private val aivenBootstrapServers: String,
) : DisposableBean {

    companion object {
        private val log = PoppLogger(this::class.java)
    }

    private lateinit var kafkaTemplateInstance: KafkaTemplate<String, String>

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        this.kafkaTemplateInstance = KafkaTemplate(producerFactory())
        return this.kafkaTemplateInstance
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(producerConfig() + securityConfig())

    private fun producerConfig() = mapOf(
        ProducerConfig.CLIENT_ID_CONFIG to "pensjon-opptjening-hendelse-api",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to aivenBootstrapServers,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
        ProducerConfig.TRANSACTIONAL_ID_CONFIG to "pensjon-opptjening-hendelse-api-tx-${UUID.randomUUID()}"
    )

    private fun securityConfig() = mapOf(
        SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to keystorePath,
        SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to credstorePassword,
        SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to credstorePassword,
        SslConfigs.SSL_KEY_PASSWORD_CONFIG to credstorePassword,
        SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG to "JKS",
        SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to "PKCS12",
        SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to truststorePath,
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SSL"
    )

    @PreDestroy
    fun close() {
        log.info("Gracefully flushing and closing Kafka producer")
        if (::kafkaTemplateInstance.isInitialized) {
            kafkaTemplateInstance.flush()
            kafkaTemplateInstance.destroy()
        }
    }

    override fun destroy() {
        log.info("Closing Kafka config")
        close()
    }
}