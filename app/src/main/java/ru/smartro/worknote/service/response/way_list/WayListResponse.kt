package ru.smartro.worknote.service.response.way_list


import com.google.gson.annotations.SerializedName


data class WayListResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("success")
    val success: Boolean
)