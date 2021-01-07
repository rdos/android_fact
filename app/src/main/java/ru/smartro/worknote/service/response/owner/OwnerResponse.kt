package ru.smartro.worknote.service.response.owner


import com.google.gson.annotations.SerializedName


data class OwnerResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)