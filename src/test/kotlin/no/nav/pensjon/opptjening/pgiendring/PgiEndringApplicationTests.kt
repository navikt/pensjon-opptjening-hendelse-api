package no.nav.pensjon.opptjening.pgiendring

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles(profiles = ["local"])
@AutoConfigureMockMvc
class PgiEndringApplicationTests {

    @Test
    fun contextLoads() {
    }

}
