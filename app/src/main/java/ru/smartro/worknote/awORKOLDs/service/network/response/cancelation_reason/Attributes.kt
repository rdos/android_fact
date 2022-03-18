package ru.smartro.worknote.awORKOLDs.service.network.response.cancelation_reason


import com.google.gson.annotations.SerializedName


data class Attributes(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("organisation_id")
    val organisationId: Int
)