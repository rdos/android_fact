package ru.smartro.worknote.service.network.body.breakdown


import com.google.gson.annotations.SerializedName


data class BreakDownItem(
    @SerializedName("allowed")
    val allowed: List<Int>,
    @SerializedName("c_id")
    val cId: Int,
    @SerializedName("co")
    val co: List<Double>,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("datetime")
    val datetime: Long,
    @SerializedName("failure_type")
    val failureType: String?,
    @SerializedName("media")
    val media: List<String>,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("p_id")
    val pId: Int,
    @SerializedName("redirect")
    val redirect: String,
    @SerializedName("t_id")
    val tId: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("wo_id")
    val woId: Int
)