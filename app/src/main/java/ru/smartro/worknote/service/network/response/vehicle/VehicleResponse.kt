package ru.smartro.worknote.service.network.response.vehicle


import com.google.gson.annotations.SerializedName


data class VehicleResponse(
    @SerializedName("data")
    val data: List<Vehicle>,
    @SerializedName("success")
    val success: Boolean
)