package ru.smartro.worknote.awORKOLDs.service.network.response.failure_reason.send_failure


import com.google.gson.annotations.SerializedName


data class FailureResultResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)