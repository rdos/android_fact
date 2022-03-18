package ru.smartro.worknote.awORKOLDs.service.network.response.cancelation_reason


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("attributes")
    val attributes: Attributes,
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String
)