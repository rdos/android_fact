package ru.smartro.worknote.awORKOLDs.service.network.response.breakdown


import com.google.gson.annotations.SerializedName


data class Attributes(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("organisation_id")
    val organisationId: Int,
    @SerializedName("trashed")
    val trashed: Boolean
)