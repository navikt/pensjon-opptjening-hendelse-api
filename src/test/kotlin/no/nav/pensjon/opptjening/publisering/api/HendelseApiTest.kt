package no.nav.pensjon.opptjening.publisering.api

import com.nimbusds.jose.JOSEObjectType
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
internal class HendelseApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var server: MockOAuth2Server

    @MockBean
    private lateinit var service: HendelseService


    @Test
    fun `svarer 200 ok hvis alt går bra`() {
        mockMvc.perform(
            post("/api/hendelser")
                .contentType(APPLICATION_JSON)
                .content("[]")
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `svarer 401 hvis audience er feil`() {
        mockMvc.perform(
            post("/api/hendelser")
                .contentType(APPLICATION_JSON)
                .content("")
                .header(HttpHeaders.AUTHORIZATION, token("faultyAudClaim"))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `svarer 500 hvis publisering feiler`() {
        whenever(service.handle(any())).thenThrow(PublishFailedException("the message", RuntimeException("something")))

        mockMvc.perform(
            post("/api/hendelser")
                .contentType(APPLICATION_JSON)
                .content("[]")
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().json("""{"message":"the message"}"""))
    }

    @Test
    fun `parser en liste med hendelser til en liste med strenger`() {
        val hendelserJson = """[{"fnr":"12341234123",
                | "arsak":"INNTEKT",
                | "hendelsesTid":"2021-01-01T15:16:17+01:00[Europe/Oslo]",
                | "fom":"2010-01-01",
                | "tom":"2020-12-31",
                | "type":"ENDRET_BEHOLDNING"}]""".trimMargin()
        mockMvc.perform(
            post("/api/hendelser")
                .contentType(APPLICATION_JSON)
                .content(hendelserJson)
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(status().isOk)
    }

    private fun token(audience: String): String {
        return "Bearer ${
            server.issueToken(
                issuerId = "aad",
                clientId = "theclientid",
                tokenCallback = DefaultOAuth2TokenCallback(
                    "aad",
                    "random",
                    JOSEObjectType.JWT.type,
                    listOf(audience),
                    emptyMap(),
                    3600
                )
            ).serialize()
        }"
    }
}