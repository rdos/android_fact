package ru.smartro.worknote.service.network.response.failure_reason.send_failure


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("p_id")
    val pId: Int
)