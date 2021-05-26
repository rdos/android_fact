package ru.smartro.worknote.service.network.response.work_order

import com.google.gson.annotations.SerializedName


data class WorkOrderResponse(
    @SerializedName("data")
    val data: WorkOrderData,
    @SerializedName("success")
    val success: Boolean
)

data class Workorder(
    @SerializedName("id")
    val id: Int,
    @SerializedName("accounting")
    val accounting: Int,
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @SerializedName("finished_at")
    val finishedAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("platforms")
    val platforms: List<Platform>,
    @SerializedName("start")
    val start: Start,
    @SerializedName("unload")
    val unload: Unload
)

data class Unload(
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class Start(
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class Platform(
    @SerializedName("address")
    val address: String,
    @SerializedName("after_media")
    val afterMedia: List<String>,
    @SerializedName("before_media")
    val beforeMedia: List<String>,
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @SerializedName("containers")
    val containers: List<Container>,
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("failure_media")
    val failureMedia: List<String>,
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @SerializedName("finished_at")
    val finishedAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("srp_id")
    val srpId: Int
)

data class WorkOrderData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("workorders")
    val workorders: List<Workorder>
)

data class Container(
    @SerializedName("client")
    val client: String,
    @SerializedName("contacts")
    val contacts: String,
    @SerializedName("failure_media")
    val failureMedia: List<String>,
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active_today")
    val isActiveToday: Boolean,
    @SerializedName("number")
    val number: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type_id")
    val typeId: Int,
    @SerializedName("volume")
    val volume: Double
)