package ru.smartro.worknote.log.awORKOLDs.service.network.response.served


import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("ps")
    val ps: List<ServedWayPoint>
)