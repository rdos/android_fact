package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WayUnloadPoint(
    @SerializedName("co")
    val co: List<Double>,
    @SerializedName("name")
    val name: String
) : Serializable