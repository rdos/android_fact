package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WayStartPoint(
    @SerializedName("co")
    val coordinates: List<Double>,
    @SerializedName("name")
    val name: String
): Serializable