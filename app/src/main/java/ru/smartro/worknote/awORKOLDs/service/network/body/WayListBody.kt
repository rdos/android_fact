package ru.smartro.worknote.awORKOLDs.service.network.body


import com.google.gson.annotations.SerializedName


data class WayListBody(
    @SerializedName("date")
    val date: String,
    @SerializedName("oid")
    val organisationId: Int,
    @SerializedName("v_id")
    val vehicleId: Int
)