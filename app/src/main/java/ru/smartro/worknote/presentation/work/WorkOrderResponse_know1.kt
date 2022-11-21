package ru.smartro.worknote.presentation.work

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class WorkOrderResponse_know1(
    @SerializedName("data")
    val dataKnow100: WorkOrderData_know100,
    @SerializedName("success")
    val success: Boolean
)

data class WoRKoRDeR_know1(
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
    val waste_type: WasteType_know1?,
    @SerializedName("platforms")
    val platformKnow1s: List<Platform_know1>,
    @SerializedName("start")
    val STaRTknow1: STaRT_know1,
    @SerializedName("unload")
    val uNLoaDknow1: Unload_know1
)

data class Unload_know1(
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class WasteType_know1(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: Color_know1
)

data class Color_know1(
    val hex: String
)

data class STaRT_know1(
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class Platform_know1(
    @SerializedName("address")
    val address: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("icon")
    val icon : String,
    @SerializedName("after_media")
    val afterMedia: List<String>,
    @SerializedName("before_media")
    val beforeMedia: List<String>,
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @SerializedName("containers")
    val coNTaiNeRKnow1s: List<CoNTaiNeR_know1>,
    @SerializedName("coords")
    val coords: List<Double>,
    @SerializedName("failure_media")
    val failureMedia: List<String>? = null,
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @SerializedName("breakdown_reason_id")
    val breakdownReasonId: Int,
    @SerializedName("finished_at")
    val finishedAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("updated_at")
    var updateAt: Long,
    @SerializedName("srp_id")
    val srpId: Int,
    @SerializedName("order_start_time")
    var orderStartTime: String? = null,
    @SerializedName("order_end_time")
    var orderEndTime: String? = null,
    @SerializedName("order_warning_time")
    var orderWarningTime: String? = null,
    @SerializedName("order_alert_time")
    var orderAlertTime: String? = null,

    @SerializedName("need_cleanup")
    var needCleanup: Boolean = false,
    @SerializedName("was_cleaned_up")
    var wasCleanedUp: Boolean = false,

//    var needCleanupWasShown: Boolean = false,

    var kgo_remaining: KGOEntity_know100? = null,
    var kgo_served: KGOEntity_know100? = null,

)

data class KGOEntity_know100(
    var volume: Double?,
    var media: List<String>?
)

data class WorkOrderData_know100(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("workorders")
    val woRKoRDeRknow1s: List<WoRKoRDeR_know1>
)

data class CoNTaiNeR_know1(
    @SerializedName("client")
    val client: String,
    @SerializedName("contacts")
    val contacts: String,
    @SerializedName("icon")
    val icon : String,
    @SerializedName("failure_media")
    val failureMedia: List<String>? = null,
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @SerializedName("breakdown_media")
    val breakdownMedia: List<String>? = null,
    @SerializedName("breakdown_reason_id")
    val breakdownReasonId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active_today")
    val isActiveToday: Boolean,
    @SerializedName("number")
    val number: String,
    @SerializedName("constructive_volume")
    var constructiveVolume: Double? = null,
    @SerializedName("type_name")
    var typeName: String? = null,
    @SerializedName("status")
    val status: String,
    @SerializedName("type_id")
    val typeId: Int,
    @SerializedName("volume")
    val volume: Double? = null
)