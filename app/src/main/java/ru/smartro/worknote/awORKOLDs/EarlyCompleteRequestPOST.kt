package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.SerializedName
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import kotlin.reflect.KClass

class EarlyCompleteRequestPOST: POSTRequestA<EarlyCompleteBodyIn, EarlyCompleteBodyOut>() {
    override fun onGetSRVName(): String {
        return "workorder/{workOrderId}/early_complete"
    }

    override fun onGetRequestBodyIn(): EarlyCompleteBodyIn {

        return EarlyCompleteBodyIn(123, MyUtil.timeStampInSec(), 1, 1.0)
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
    @SerializedName("failure_id")
    val failureId: Int,
    @SerializedName("finished_at")
    val finishedAt: Long,
    @SerializedName("unload_type")
    val unloadType: Int,
    @SerializedName("unload_value")
    val unloadValue: Double
): NetObject()

data class EarlyCompleteBodyOut(val success: Boolean, val message : String): NetObject()