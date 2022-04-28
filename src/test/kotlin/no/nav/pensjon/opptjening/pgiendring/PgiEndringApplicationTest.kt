package no.nav.pensjon.opptjening.pgiendring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nimbusds.jose.JOSEObjectType
import no.nav.pensjon.opptjening.pgiendring.api.PgiEndring
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [PgiEndringApplication::class])
@ActiveProfiles(profiles = ["local"])
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = ["pgi-endring-topic"])
@EnableMockOAuth2Server
internal class PgiEndringApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var consumer: TestKafkaConsumer

    @Autowired
    private lateinit var server: MockOAuth2Server


    @Test
    fun `Add pgi-endring returns 200 ok`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring())
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Add pgi-endring adds record to topic`() {
        val inntektAar = 2010
        val foedselsnummer = """"3333333333333""""
        val opptjeningType = """"SUM_TEST""""

        val pgiEndring = createPgiEndring(aar = inntektAar, fnr = foedselsnummer, opptjeningType = opptjeningType)

        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(pgiEndring)
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
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
        assert(key.contains(opptjeningType))

    }


    @Test
    fun `Returns 400 if aar is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(aar = null))
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Returns 400 if fnr is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(fnr = null))
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Returns 400 if opptjeningType is null`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring(opptjeningType = null))
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Returns 401 if audience does not match accepted_audience`() {
        mockMvc.perform(
            post("/pgi/publiser/endring")
                .contentType(APPLICATION_JSON)
                .content(createPgiEndring())
                .header(HttpHeaders.AUTHORIZATION, token("faultyAudClaim"))
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
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

    private fun token(audience: String): String {
        return "Bearer ${
            server.issueToken(
                issuerId = "aad",
                clientId = "theclientid",
                tokenCallback = DefaultOAuth2TokenCallback("aad", "random", JOSEObjectType.JWT.type, listOf(audience), emptyMap(), 3600)
            ).serialize()
        }"
    }
}