package ru.smartro.worknote.network.workNote.requestDTO

import com.squareup.moshi.Json

data class WaybillBodyRequest(
    @Json(name = "organisation_id") val organisationId: Int
)