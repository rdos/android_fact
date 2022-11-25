package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.work.RealmRepository
import kotlin.reflect.KClass

class CompleteRequestPOST(val workOrderId: Int) : AbsRequest<CompleteWayBodyIn, CompleteWayBodyOut>() {
    override fun onGetSRVName(): String {
        return "workorder/${workOrderId}/complete"
    }

    override fun onGetRequestBodyIn(): CompleteWayBodyIn {
        val db = RealmRepository(Realm.getDefaultInstance())
        val workOrderEntity = db.getWorkOrderEntity(workOrderId)
        val result = workOrderEntity.getCompleteBodyIn()
        return result
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: CompleteWayBodyOut) {

    }

    override fun onGetResponseClazz(): KClass<CompleteWayBodyOut> {
        return CompleteWayBodyOut::class
    }
}


data class CompleteWayBodyIn(
    @Expose
    @SerializedName("finished_at")
    val finishedAt: Long,
    @SerializedName("unload_type")
    @Expose
    val unloadType: Int,
    @SerializedName("unload_value")
    @Expose
    // TODO !!!!!! WAS STRING??
    val unloadValue: Double
): NetObject()

data class CompleteWayBodyOut(val success: Boolean, val message : String): NetObject()

