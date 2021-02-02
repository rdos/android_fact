package ru.smartro.worknote.service.response.vehicle


import com.google.gson.annotations.SerializedName


data class Vehicle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("oid")
    val oid: Int
)