package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("wos")
    val wos: List<WayInfo>
)