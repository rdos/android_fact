package ru.smartro.worknote.awORKOLDs.service.network.response.vehicle


import com.google.gson.annotations.SerializedName


data class VehicleResponse(
    @SerializedName("data")
    val data: List<Vehicle>,
    @SerializedName("success")
    val success: Boolean
)