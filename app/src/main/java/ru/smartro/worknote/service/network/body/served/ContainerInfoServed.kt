package ru.smartro.worknote.service.network.body.served


import com.google.gson.annotations.SerializedName


data class ContainerInfoServed(
    @SerializedName("c_id")
    val cId: Int,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("oid")
    val oid: String,
    @SerializedName("volume")
    val volume: Double,
    @SerializedName("wo_id")
    val woId: Int
)