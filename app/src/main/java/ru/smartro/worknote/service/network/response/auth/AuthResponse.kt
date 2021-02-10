package ru.smartro.worknote.service.network.response.auth


import com.google.gson.annotations.SerializedName


data class AuthResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)

data class Data(
    @SerializedName("token")
    val token: String
)

