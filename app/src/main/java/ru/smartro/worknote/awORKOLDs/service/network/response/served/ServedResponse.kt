package ru.smartro.worknote.awORKOLDs.service.network.response.served


import com.google.gson.annotations.SerializedName


data class ServedResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)