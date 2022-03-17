package ru.smartro.worknote.workold.service.network.response.organisation


import com.google.gson.annotations.SerializedName


data class OrganisationResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)