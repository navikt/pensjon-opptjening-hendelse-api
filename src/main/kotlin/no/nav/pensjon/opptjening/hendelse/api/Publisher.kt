package no.nav.pensjon.opptjening.hendelse.api

interface Publisher {
    fun publish(hendelser: List<Pair<Type, String>>): List<Long>
}

data class PublishFailedException(val msg: String, val ex: Exception) : RuntimeException(msg, ex)

