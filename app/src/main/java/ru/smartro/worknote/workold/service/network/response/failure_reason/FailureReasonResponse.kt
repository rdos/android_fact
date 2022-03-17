package ru.smartro.worknote.workold.service.network.response.failure_reason


import com.google.gson.annotations.SerializedName


data class FailureReasonResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)