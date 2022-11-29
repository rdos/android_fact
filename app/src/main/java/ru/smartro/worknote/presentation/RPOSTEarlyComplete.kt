package ru.smartro.worknote.presentation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.log.work.RealmRepository
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject
import kotlin.reflect.KClass

class RequestPOSTEarlyComplete(val workOrderId: Int) : AbsRequest<EarlyCompleteBodyIn, EarlyCompleteBodyOut>() {
    override fun onGetSRVName(): String {
        return "workorder/${workOrderId}/early_complete"
    }

    override fun onGetRequestBodyIn(): EarlyCompleteBodyIn {
        val db = RealmRepository(Realm.getDefaultInstance())
        val workOrderEntity = db.getWorkOrderEntity(workOrderId)
        val result = workOrderEntity.getEarlyCompleteBodyIn()
        return result
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {

    }

    override fun onBefore() {

    }

    override fun onAfter(bodyOut: EarlyCompleteBodyOut) {

    }

    override fun onGetResponseClazz(): KClass<EarlyCompleteBodyOut> {
        return EarlyCompleteBodyOut::class
    }
}


data class EarlyCompleteBodyIn(
    @Expose
    @SerializedName("failure_id")
    val failureId: Int,
    @Expose
    @SerializedName("finished_at")
    val finishedAt: Long,
    @Expose
    @SerializedName("unload_type")
    val unloadType: Int,
    @Expose
    @SerializedName("unload_value")
    val unloadValue: Double
): NetObject()

data class EarlyCompleteBodyOut(val success: Boolean, val message : String): NetObject()