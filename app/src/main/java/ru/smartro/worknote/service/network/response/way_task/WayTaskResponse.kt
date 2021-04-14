package ru.smartro.worknote.service.network.response.way_task


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WayTaskResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)

data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("wos")
    val wos: List<WayInfo>
)

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
    val platforms: List<Platform>,
    @SerializedName("start")
    val start: WayStartPoint,
    @SerializedName("unload")
    val unload: WayUnloadPoint
) : Serializable


data class Platform(
    @SerializedName("address")
    val address: String,
    @SerializedName("co")
    val coordinate: List<Double>,
    @SerializedName("cs")
    val containers: List<ContainerInfo>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("srp_id")
    val srpId: Int
): Serializable

data class WayStartPoint(
    @SerializedName("co")
    val coordinates: List<Double>,
    @SerializedName("name")
    val name: String
): Serializable

data class WayUnloadPoint(
    @SerializedName("co")
    val coordinates: List<Double>,
    @SerializedName("name")
    val name: String
) : Serializable