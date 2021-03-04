package ru.smartro.worknote.service.network.body.early_complete


import com.google.gson.annotations.SerializedName


data class EarlyCompleteBody(
    @SerializedName("datetime")
    val datetime: Long,
    @SerializedName("failure_id")
    val failureId: Int
)