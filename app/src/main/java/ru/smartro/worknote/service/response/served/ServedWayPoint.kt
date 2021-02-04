package ru.smartro.worknote.service.response.served


import com.google.gson.annotations.SerializedName


data class ServedWayPoint(
    @SerializedName("id")
    val id: Int,
    @SerializedName("p_id")
    val pId: Int
)