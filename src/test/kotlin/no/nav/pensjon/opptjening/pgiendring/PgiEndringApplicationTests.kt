package no.nav.pensjon.opptjening.pgiendring

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest()
@ActiveProfiles(profiles = ["dev-gcp"])
@AutoConfigureMockMvc
class PgiEndringApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnDefaultMessage() {
        mockMvc.perform(get("/pgi/ping")).andExpect(status().isOk)
    }

}
