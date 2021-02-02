package ru.smartro.worknote.service.body


import com.google.gson.annotations.SerializedName


data class WayListBody(
    @SerializedName("date")
    val date: String,
    @SerializedName("oid")
    val organisationId: Int,
    @SerializedName("v_id")
    val vehicleId: Int
)