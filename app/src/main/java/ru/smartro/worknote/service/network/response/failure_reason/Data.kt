package ru.smartro.worknote.service.network.response.failure_reason


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("oid")
    val oid: Int
)