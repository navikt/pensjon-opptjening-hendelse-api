package no.nav.pensjon.opptjening.hendelse.api

import tools.jackson.databind.JsonNode
import no.nav.pensjon.opptjening.hendelse.kafka.EndringsType
import org.apache.kafka.clients.producer.RecordMetadata

data class MottattHendelse(
    val json: JsonNode,
) {
    val jsonString: String = json.toString()
    val id: String = json.get("id").stringValue()
    val type: EndringsType = EndringsType.valueOf(json.get("type").stringValue())
}

data class PublisertHendelse(
    val hendelse: MottattHendelse,
    val recordMetadata: RecordMetadata,
) {
    override fun toString(): String {
        return "PublisertHendelse {'hendelseId':${hendelse.id}, 'metadata': ${recordMetadata}}"
    }
}