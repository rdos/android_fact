package ru.smartro.worknote.service.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ContainerInfo(
    @SerializedName("client")
    val client: String,
    @SerializedName("contacts")
    val contacts: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("type_id")
    val typeId: Int
) : Serializable