package ru.smartro.worknote.service.network.response.served


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("ps")
    val ps: List<ServedWayPoint>
)