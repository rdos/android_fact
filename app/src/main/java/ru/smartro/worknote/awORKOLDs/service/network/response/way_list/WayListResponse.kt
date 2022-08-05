package ru.smartro.worknote.awORKOLDs.service.network.response.way_list


import com.google.gson.annotations.SerializedName


data class WayListResponse(
    @SerializedName("data")
    val data: List<WayBillDto>,
    @SerializedName("success")
    val success: Boolean
)