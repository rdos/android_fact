package ru.smartro.worknote.awORKOLDs.service.network.response.breakdown


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("attributes")
    val attributes: Attributes,
    @SerializedName("id")
    val id: Int,
    @SerializedName("relationships")
    val relationships: List<Any>,
    @SerializedName("type")
    val type: String
)