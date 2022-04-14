package ru.smartro.worknote.awORKOLDs.service.network.response.way_list


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("driver")
    val driver: String,
    @SerializedName("route_name")
    val route_name: String,
    @SerializedName("oid")
    val oid: Int
)