package no.nav.pensjon.opptjening.hendelse.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.pensjon.opptjening.hendelse.api.MottattHendelse
import no.nav.pensjon.opptjening.hendelse.api.PublisertHendelse
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.kafka.core.KafkaOperations.OperationsCallback
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

class KafkaPublisherTest {

    private val kafkaTemplate: KafkaTemplate<String, String> = mock()
    private val customProducerListener: CustomProducerListener = CustomProducerListener()
    private val beholdningEndretTopic = "bet"
    private val opptjeningEndretTopic = "oet"

    private val publisher = KafkaPublisher(
        kafkaTemplate = kafkaTemplate,
        customProducerListener = customProducerListener,
        beholdningEndretTopic = beholdningEndretTopic,
        opptjeningEndretTopic = opptjeningEndretTopic
    )

    @BeforeEach
    fun beforeEach() {
        whenever(kafkaTemplate.executeInTransaction(any<OperationsCallback<String, String, String>>())).thenAnswer {
            (it.arguments[0] as OperationsCallback<String, String, String>).doInOperations(kafkaTemplate)
        }
    }

    @Test
    fun `svarer med offset dersom alt går bra`() {
        val recordMetadata = RecordMetadata(TopicPartition(beholdningEndretTopic, 1), 12345L, 0, 0, 0, 0)

        whenever(kafkaTemplate.send(any(), any())).thenAnswer { invocation ->
            CompletableFuture.completedFuture(
                SendResult(
                    ProducerRecord<String, String>(
                        invocation.arguments[0] as String?,
                        invocation.arguments[1] as String?
                    ),
                    recordMetadata
                )
            )
        }

        val mottatt = MottattHendelse(
            json = jacksonObjectMapper().readTree(
                """
                {
                    "id":"whatever",
                    "type":"ENDRET_BEHOLDNING"                 
                }
                """.trimIndent()
            )
        )

        val actual = publisher.publish(listOf(mottatt))
        val expected = listOf(PublisertHendelse(mottatt, recordMetadata))

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `kaster exception dersom resultat fra future ikke fullført ok`() {
        whenever(kafkaTemplate.send(any(), any())).thenAnswer { invocation ->
            CompletableFuture.failedFuture<Any>(RuntimeException())
        }

        val mottatt = MottattHendelse(
            json = jacksonObjectMapper().readTree(
                """
                {
                    "id":"whatever",
                    "type":"ENDRET_BEHOLDNING"                 
                }
                """.trimIndent()
            )
        )

        assertThrows<Exception> {
            publisher.publish(listOf(mottatt))
        }
    }
}