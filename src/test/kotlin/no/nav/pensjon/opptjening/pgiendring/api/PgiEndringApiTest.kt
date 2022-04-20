package no.nav.pensjon.opptjening.pgiendring.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
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
                .content(PGI_ENDRING)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }


    companion object {
        const val PGI_ENDRING = """
            {
                "aar":2021,
                "fnr":"12345678901",
                "opptjeningType":"SUM_PI"
            }
        """
    }


}