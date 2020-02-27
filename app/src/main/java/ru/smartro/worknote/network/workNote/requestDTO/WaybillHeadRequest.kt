package ru.smartro.worknote.network.workNote.requestDTO

import com.squareup.moshi.Json

data class WaybillHeadRequest(
    val date: String,
    @Json(name = "vehicle_id") val vehicleId: Int,
    @Json(name = "organisation_id") val organisationId: Int
)