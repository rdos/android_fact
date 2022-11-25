package ru.smartro.worknote.awORKOLDs

import com.google.gson.annotations.SerializedName
import io.realm.Realm
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.work.RealmRepository
import kotlin.reflect.KClass

//class CompleteRequestPOST(val workOrderId: Int) : AbsRequest<EarlyCompleteBodyIn, EarlyCompleteBodyOut>() {
//    override fun onGetSRVName(): String {
//        return "workorder/{workOrderId}/early_complete"
//    }
//
//    override fun onGetRequestBodyIn(): EarlyCompleteBodyIn {
//        val db = RealmRepository(Realm.getDefaultInstance())
//        val workOrderEntity = db.getWorkOrderEntity(workOrderId)
//        val result = workOrderEntity.getEarlyCompleteBodyIn()
//        return result
//    }
//
//    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
//
//    }
//
//    override fun onBefore() {
//
//    }
//
//
//}


