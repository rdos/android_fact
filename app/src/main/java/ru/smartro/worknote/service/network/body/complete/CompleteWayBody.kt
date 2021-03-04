package ru.smartro.worknote.service.network.body.complete


import com.google.gson.annotations.SerializedName


data class CompleteWayBody(
    @SerializedName("finished_at")
    val finishedAt: Long,
    @SerializedName("unload_type")
    val unloadType: Int,
    @SerializedName("unload_value")
    val unloadValue: String
)