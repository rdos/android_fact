package ru.smartro.worknote.awORKOLDs.service.network.response.way_list


import com.google.gson.annotations.SerializedName


data class WayBillDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int
)