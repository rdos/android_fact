package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName


data class WayTaskResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)