package ru.smartro.worknote.service.response.served


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("ps")
    val ps: List<ServedWayPoint>
)