package no.nav.pensjon.opptjening.hendelse.api

import com.fasterxml.jackson.databind.JsonNode
import no.nav.pensjon.opptjening.hendelse.kafka.EndringsType

data class MottattHendelse(
    val json: JsonNode,
) {
    val jsonString: String = json.toString()
    val id: String = json.get("id").textValue()
    val type: EndringsType = EndringsType.valueOf(json.get("type").textValue())
}

data class PublisertHendelse(
    val hendelse: MottattHendelse,
    val offset: Long,
) {
    override fun toString(): String {
        return "PublisertHendelse ['hendelseId':${hendelse.id}, 'offset':$offset]"
    }
}