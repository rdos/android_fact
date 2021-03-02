package ru.smartro.worknote.service.network.body.failure


import com.google.gson.annotations.SerializedName


data class FailureItem(
    @SerializedName("co")
    val co: List<Double>,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("datetime")
    val datetime: Long,
    @SerializedName("failure_id")
    val failureId: Int,
    @SerializedName("failure_type")
    val failureType: String,
    @SerializedName("media")
    val media: List<String>,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("p_id")
    val pId: Int,
    @SerializedName("wo_id")
    val woId: Int
)