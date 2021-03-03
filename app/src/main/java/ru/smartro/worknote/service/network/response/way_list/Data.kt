package ru.smartro.worknote.service.network.response.way_list


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int
)