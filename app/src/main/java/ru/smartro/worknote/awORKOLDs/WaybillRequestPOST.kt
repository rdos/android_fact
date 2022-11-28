package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.work.RealmRepository
import ru.smartro.worknote.presentation.work.WaybillEntity
import kotlin.reflect.KClass

class WaybillRequestPOST : AbsRequest<WaybillBodyIn, WaybillBodyOut>() {
    override fun onGetSRVName(): String {
        return "waybill"
    }

    override fun onGetRequestBodyIn(): WaybillBodyIn {
        val result = WaybillBodyIn(
            date = MyUtil.currentDate(),
            organisationId = App.getAppParaMS().getOwnerId(),
            vehicleId = App.getAppParaMS().getVehicleId()
        )
        return result
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: WaybillBodyOut) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val organisationId = App.getAppParaMS().getOwnerId()
        val vehicleId = App.getAppParaMS().getVehicleId()
        val waybillEntityS = RealmList<WaybillEntity>()
        for(data in bodyOut.data) {
            val waybillEntity = WaybillEntity()
            waybillEntity.id = data.id
            waybillEntity.number = data.number?:"номер не указан"
            waybillEntity.organizationId = organisationId
            waybillEntity.vehicleId = vehicleId
            waybillEntityS.add(waybillEntity)
        }
        db.setWaybillEntity(waybillEntityS)
    }

    override fun onGetResponseClazz(): KClass<WaybillBodyOut> {
        return WaybillBodyOut::class
    }
}


data class WaybillBodyIn(
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("oid")
    val organisationId: Int,
    @Expose
    @SerializedName("v_id")
    val vehicleId: Int
): NetObject()

data class WaybillBodyOut(
    @Expose
    @SerializedName("data")
    val data: List<WaybillBodyOutData>,
    @Expose
    @SerializedName("success")
    val success: Boolean
): NetObject()

data class WaybillBodyOutData(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("number")
    val number: String?= null,
    @Expose
    @SerializedName("oid")
    val oid: Int
): NetObject()