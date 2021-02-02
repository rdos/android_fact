package ru.smartro.worknote.service.body.served


import com.google.gson.annotations.SerializedName


data class ContainerPoint(
    @SerializedName("beginned_at")
    val beginnedAt: Int,
    @SerializedName("co")
    val co: List<Double>,
    @SerializedName("cs")
    val cs: List<ContainerInfo>,
    @SerializedName("finished_at")
    val finishedAt: Int,
    @SerializedName("media_after")
    val mediaAfter: ArrayList<String>,
    @SerializedName("media_before")
    val mediaBefore: List<String>,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("p_id")
    val pId: Int,
    @SerializedName("wo_id")
    val woId: Int
)