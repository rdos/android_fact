package ru.smartro.worknote.service.network.body.served


import com.google.gson.annotations.SerializedName


data class ContainerPointServed(
    @SerializedName("beginned_at")
    val beginnedAt: Long,
    @SerializedName("co")
    val co: List<Double>,
    @SerializedName("cs")
    val cs: List<ContainerInfoServed>,
    @SerializedName("finished_at")
    val finishedAt: Long,
    @SerializedName("media_after")
    val mediaAfter: List<String>,
    @SerializedName("media_before")
    val mediaBefore: List<String>,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("p_id")
    val pId: Int,
    @SerializedName("wo_id")
    val woId: Int
)