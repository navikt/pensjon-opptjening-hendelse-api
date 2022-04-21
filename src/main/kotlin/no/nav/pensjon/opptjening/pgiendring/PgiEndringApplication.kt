package no.nav.pensjon.opptjening.pgiendring

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile

@SpringBootApplication
@EnableJwtTokenValidation
@Profile("dev-gcp", "prod-gcp")
class PgiEndringApplication

fun main(args: Array<String>) {
    runApplication<PgiEndringApplication>(*args)
}
