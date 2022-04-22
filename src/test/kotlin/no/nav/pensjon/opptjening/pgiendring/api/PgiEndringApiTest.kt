package no.nav.pensjon.opptjening.pgiendring.api


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.pensjon.opptjening.pgiendring.TestApplication
import no.nav.pensjon.opptjening.pgiendring.TestKafkaConsumer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.concurrent.TimeUnit


@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles(profiles = ["local"])
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = ["pgi-endring-topic"])
internal class PgiEndringApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Value("\${KAFKA_PGI_ENDRING_TOPIC}")
    private lateinit var pgiEndringTopic: String

    @Value("\${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    private lateinit var brokers: String

    @Autowired
    private lateinit var consumer: TestKafkaConsumer


    @Test
    fun `Add pgi-endring returns 200 ok`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Add pgi-endring adds record to topic`() {
        val inntektAar = 2010
        val foedselsnummer = "3333333333333"

        val pgiEndring = createPgiEndring(aar = inntektAar, fnr = foedselsnummer)

        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(pgiEndring)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        consumer.latch.await(10000, TimeUnit.MILLISECONDS)
        val record = consumer.getLastConsumedRecord()

        val input = pgiEndring.toPgiEndringObject()
        val onTopic = record.value().toPgiEndringObject()

        assertEquals(input.aar, onTopic.aar)
        assertEquals(input.fnr, onTopic.fnr)
        assertEquals(input.opptjeningType, onTopic.opptjeningType)

        val key = record.key()
        assert(key.contains("$inntektAar"))
        assert(key.contains(foedselsnummer))
    }


    @Test
    fun `Returns 400 if aar is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(aar = null))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Returns 400 if fnr is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(fnr = null))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Returns 400 if opptjeningType is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(opptjeningType = null))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    private fun String.toPgiEndringObject(): PgiEndring = ObjectMapper().registerModule(KotlinModule.Builder().build()).readValue(this, PgiEndring::class.java)


    private fun createPgiEndring(aar: Int? = 2021, fnr: String? = """"12345678901"""", opptjeningType: String? = """"SUM_PI""""): String {
        return """
            {
                "aar":$aar,
                "fnr":$fnr,
                "opptjeningType":$opptjeningType
            }
        """
    }
}