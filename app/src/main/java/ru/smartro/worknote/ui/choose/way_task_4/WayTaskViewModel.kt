package ru.smartro.worknote.ui.choose.way_task_4

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import io.realm.RealmModel
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.breakdown.BreakDownResponse
import ru.smartro.worknote.service.network.response.cancelation_reason.CancelationReasonResponse
import ru.smartro.worknote.service.network.response.failure_reason.FailureReasonResponse
import ru.smartro.worknote.service.network.response.work_order.WorkOrderResponse
import ru.smartro.worknote.service.network.response.work_order.Workorder

class WayTaskViewModel(application: Application) : BaseViewModel(application) {

/*
    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }
*/

    fun getWorkOrder(organisationId: Int, wayId: Int): LiveData<Resource<WorkOrderResponse>> {
        Log.d("AAA", "getWorkOrder")
        return network.getWorkOder(organisationId, wayId)
    }

    fun getBreakDownTypes(): LiveData<Resource<Nothing>> {
        return network.getBreakDownTypes()
    }


    fun getFailReason(): LiveData<Resource<Nothing>> {
        return network.getFailReason()
    }

    fun getCancelWayReason(): LiveData<Resource<Nothing>> {
        return network.getCancelWayReason()
    }

    fun insertWayTask(response: Workorder) {
            db.insertWayTask(response)
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return db.createObjectFromJson(clazz, json)
    }

    fun progress(id: Int, body: ProgressBody): LiveData<Resource<EmptyResponse>> {
        return network.progress(id, body)
    }

}

