package ru.smartro.worknote.service.network.response.breakdown


import com.google.gson.annotations.SerializedName


data class BreakDownResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("success")
    val success: Boolean
)