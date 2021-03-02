package ru.smartro.worknote.service.network.response.breakdown.sendBreakDown


import com.google.gson.annotations.SerializedName


data class BreakDownResultResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("success")
    val success: Boolean
)