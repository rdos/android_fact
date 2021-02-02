package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WayPoint(
    @SerializedName("address")
    val address: String,
    @SerializedName("co")
    val coordinate: List<Double>,
    @SerializedName("cs")
    val containerInfo: List<ContainerInfo>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("srp_id")
    val srpId: Int
): Serializable