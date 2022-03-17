package ru.smartro.worknote.workold.service.network.response.cancelation_reason


import com.google.gson.annotations.SerializedName


data class CancelationReasonResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)