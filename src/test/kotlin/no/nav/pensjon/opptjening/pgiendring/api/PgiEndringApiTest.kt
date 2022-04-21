package no.nav.pensjon.opptjening.pgiendring.api


import no.nav.pensjon.opptjening.pgiendring.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles(profiles = ["local"])
@AutoConfigureMockMvc
internal class PgiEndringApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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