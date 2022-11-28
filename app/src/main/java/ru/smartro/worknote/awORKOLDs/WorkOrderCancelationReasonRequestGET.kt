package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.App
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.presentation.work.RealmRepository
import ru.smartro.worknote.presentation.work.net.CancelWayReasonEntity
import kotlin.reflect.KClass

class WorkOrderCancelationReasonRequestGET :GETRequestA<WorkOrderCancelationReasonBodyOut>() {

    override fun onGetSRVName(): String {
        return "work_order_cancelation_reason"
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: WorkOrderCancelationReasonBodyOut) {
        val db = RealmRepository(Realm.getDefaultInstance())
        val organisationId = App.getAppParaMS().getOwnerId()
        val entities = mutableListOf<CancelWayReasonEntity>()
        bodyOut?.data?.forEach {
            if(it.attributes.organisationId == organisationId) {
                val newCancelWayReasonEntity = CancelWayReasonEntity(it.id, it.attributes.name)
                entities.add(newCancelWayReasonEntity)
            }
        }

        db.insertCancelWayReason(entities)
    }

    override fun onGetResponseClazz(): KClass<WorkOrderCancelationReasonBodyOut> {
        return WorkOrderCancelationReasonBodyOut::class
    }
}

data class WorkOrderCancelationReasonBodyOut(
    @Expose
    @SerializedName("data")
    val `data`: List<WorkOrderCancelationReasonBodyOutData>? = null,
    @Expose
    @SerializedName("success")
    val success: Boolean
) : NetObject()

data class WorkOrderCancelationReasonBodyOutData(
    @Expose
    @SerializedName("attributes")
    val attributes: WorkOrderCancelationReasonBodyOutDataAttributes,
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("type")
    val type: String
): NetObject()
data class WorkOrderCancelationReasonBodyOutDataAttributes(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("organisation_id")
    val organisationId: Int
): NetObject()