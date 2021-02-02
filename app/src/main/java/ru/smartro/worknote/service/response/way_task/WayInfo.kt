package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WayInfo(
    @SerializedName("accounting")
    val accounting: Int,
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @SerializedName("finished_at")
    val finishedAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("p")
    val points: List<WayPoint>,
    @SerializedName("start")
    val start: WayStartPoint,
    @SerializedName("unload")
    val unload: WayUnloadPoint
) : Serializable