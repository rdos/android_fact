package ru.smartro.worknote.awORKOLDs.service.network.body

data class PingBody(
    val type: String,
    val error: ErrorBody? = null,
    val payload: PayloadBody? = null
)

data class ErrorBody(
    val message: String? = null
)

data class PayloadBody(
    val message: String? = null
)