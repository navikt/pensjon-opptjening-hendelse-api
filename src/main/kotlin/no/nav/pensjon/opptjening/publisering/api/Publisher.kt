package no.nav.pensjon.opptjening.publisering.api

interface Publisher {
    fun publish(type: Type, message: String): Long
}

