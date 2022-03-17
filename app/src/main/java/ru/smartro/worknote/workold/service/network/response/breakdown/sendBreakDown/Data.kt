package ru.smartro.worknote.workold.service.network.response.breakdown.sendBreakDown


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("p_id")
    val pId: Int
)