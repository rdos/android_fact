package ru.smartro.worknote.presentation.work.net

import com.google.gson.annotations.SerializedName


data class EarlyCompleteBody(
    @SerializedName("failure_id")
    val failureId: Int,
    @SerializedName("finished_at")
    val finishedAt: Long,
    @SerializedName("unload_type")
    val unloadType: Int,
    @SerializedName("unload_value")
    val unloadValue: Double

)