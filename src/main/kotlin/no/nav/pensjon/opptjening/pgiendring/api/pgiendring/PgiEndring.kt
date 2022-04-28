package no.nav.pensjon.opptjening.pgiendring.api.pgiendring

data class PgiEndring(
    val aar: Int,
    val fnr: String,
    val opptjeningType: String,
) {
    init {
        require(aar != 0) {"aar is null or 0"}
    }
}

data class PgiEndringKey(
    val aar: Int,
    val fnr: String,
    val opptjeningType: String,
)

internal fun createKey(pgiEndring: PgiEndring) = PgiEndringKey(pgiEndring.aar, pgiEndring.fnr, pgiEndring.opptjeningType)