package no.nav.pensjon.opptjening.publisering.api

import com.nimbusds.jose.JOSEObjectType
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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
            post("/api/varsel")
                .contentType(APPLICATION_JSON)
                .content("{}")
                .header(HttpHeaders.AUTHORIZATION, token("testaud"))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `svarer 401 hvis audience er feil`() {
        mockMvc.perform(
            post("/api/varsel")
                .contentType(APPLICATION_JSON)
                .content("")
                .header(HttpHeaders.AUTHORIZATION, token("faultyAudClaim"))
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
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