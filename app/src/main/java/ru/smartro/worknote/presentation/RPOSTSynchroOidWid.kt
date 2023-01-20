package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.App
import ru.smartro.worknote.log.RestConnectionResource
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

//find _know1 _know100 POSTRequestA <TA, TB>()
class RPOSTSynchroOidWid : AbsRequest<ANoBodyPOST, SynchroOidWidOutBody>() {

    override fun onGetSRVName(): String {
        val organisationId = App.getAppParaMS().getOwnerId()
        val wayBillId = App.getAppParaMS().wayBillId
        val result = "synchro/${organisationId}/${wayBillId}?use_hash=1"
        return result
    }

    override fun onGetRequestBodyIn(): ANoBodyPOST {
        return ANoBodyPOST()
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: SynchroOidWidOutBody) {

    }

    override fun onGetResponseClazz(): KClass<SynchroOidWidOutBody> {
        return SynchroOidWidOutBody::class
    }
}

data class SynchroOidWidOutBody(
    @Expose
    @SerializedName("data")
    val data: SynchroOidWidOutBodyData,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class SynchroOidWidOutBodyData(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("number")
    @Expose
    val number: String,
    @SerializedName("oid")
    @Expose
    val oid: Int,
    @SerializedName("order_date")
    @Expose
    val orderDate: String,
    @Expose
    @SerializedName("workorders")
    val workOrderS: List<SynchroOidWidOutBodyDataWorkorder>
): NetObject()

data class SynchroOidWidOutBodyDataWorkorder(
    @Expose
    @SerializedName("id")
    val id: Int,
    @SerializedName("accounting")
    @Expose
    val accounting: Int,
    @Expose
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @Expose
    @SerializedName("finished_at")
    val finishedAt: String,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    val waste_type: SynchroOidWidOutBodyDataWorkorderWasteType?,
    @Expose
    @SerializedName("platforms")
    val platformKnow1s: List<SynchroOidWidOutBodyDataWorkorderPlatform>,
    @Expose
    @SerializedName("start")
    val start: SynchroOidWidOutBodyDataWorkorderStart,
    @Expose
    @SerializedName("unload")
    val uNLoaDknow1: SynchroOidWidOutBodyDataWorkorderUnload
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderPlatform(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("address")
    val address: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("icon")
    val icon : String,
    @Expose
    @SerializedName("after_media")
    val afterMedia: List<SynchroOidWidOutBodyDataWorkorderMedia>,
    @Expose
    @SerializedName("before_media")
    val beforeMedia: List<SynchroOidWidOutBodyDataWorkorderMedia>,
    @Expose
    @SerializedName("beginned_at")
    val beginnedAt: String,
    @Expose
    @SerializedName("containers")
    val coNTaiNeRKnow1s: List<SynchroOidWidOutBodyDataWorkorderPlatformContainer>,
    @Expose
    @SerializedName("coords")
    val coords: List<Double>,
    @Expose
    @SerializedName("failure_media")
    val failureMedia: List<SynchroOidWidOutBodyDataWorkorderMedia>? = null,
    @Expose
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @Expose
    @SerializedName("breakdown_reason_id")
    val breakdownReasonId: Int,
    @Expose
    @SerializedName("finished_at")
    val finishedAt: String,
    @Expose
    @SerializedName("name")
    val name: String? = null,
    @Expose
    @SerializedName("updated_at")
    var updateAt: Long,
    @Expose
    @SerializedName("srp_id")
    val srpId: Long,
    @Expose
    @SerializedName("order_start_time")
    var orderStartTime: String? = null,
    @Expose
    @SerializedName("order_end_time")
    var orderEndTime: String? = null,
    @Expose
    @SerializedName("order_warning_time")
    var orderWarningTime: String? = null,
    @Expose
    @SerializedName("order_alert_time")
    var orderAlertTime: String? = null,
    @Expose
    var kgo_remaining: SynchroOidWidOutBodyDataWorkorderPlatformKGOEntity? = null,
    @Expose
    var kgo_served: SynchroOidWidOutBodyDataWorkorderPlatformKGOEntity? = null,
    @SerializedName("cleanup")
    @Expose
    var cleanup: Boolean = false,
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderMedia(
    @Expose
    var hash: String,
    @Expose
    var link: String?
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderPlatformKGOEntity(
    @Expose
    var volume: Double?,
    @Expose
    var media: List<SynchroOidWidOutBodyDataWorkorderMedia>?
): NetObject()



data class SynchroOidWidOutBodyDataWorkorderPlatformContainer(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("client")
    val client: String,
    @Expose
    @SerializedName("contacts")
    val contacts: String,
    @Expose
    @SerializedName("icon")
    val icon : String,
    @Expose
    @SerializedName("failure_media")
    val failureMedia: List<SynchroOidWidOutBodyDataWorkorderMedia>? = null,
    @Expose
    @SerializedName("failure_reason_id")
    val failureReasonId: Int,
    @Expose
    @SerializedName("breakdown_media")
    val breakdownMedia: List<SynchroOidWidOutBodyDataWorkorderMedia>? = null,
    @Expose
    @SerializedName("breakdown_reason_id")
    val breakdownReasonId: Int,
    @Expose
    @SerializedName("is_active_today")
    val isActiveToday: Boolean,
    @Expose
    @SerializedName("number")
    val number: String,
    @Expose
    @SerializedName("constructive_volume")
    var constructiveVolume: Double? = null,
    @Expose
    @SerializedName("type_name")
    var typeName: String? = null,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("type_id")
    val typeId: Int,
    @Expose
    @SerializedName("volume")
    val volume: Double? = null
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderUnload(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("coords")
    val coords: List<Double>,
    @Expose
    @SerializedName("name")
    val name: String
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderStart(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("coords")
    val coords: List<Double>,
    @Expose
    @SerializedName("name")
    val name: String
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderWasteType(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("color")
    val color: SynchroOidWidOutBodyDataWorkorderWasteTypeColor
): NetObject()

data class SynchroOidWidOutBodyDataWorkorderWasteTypeColor(
    @Expose
    @SerializedName("hex")
    val hex: String
): NetObject()