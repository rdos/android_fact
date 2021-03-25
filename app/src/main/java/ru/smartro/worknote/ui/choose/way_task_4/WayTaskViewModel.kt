package ru.smartro.worknote.ui.choose.way_task_4

import android.app.Application
import androidx.lifecycle.LiveData
import io.realm.RealmModel
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerBreakdownEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerFailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.WayTaskBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.breakdown.BreakDownResponse
import ru.smartro.worknote.service.network.response.cancelation_reason.CancelationReasonResponse
import ru.smartro.worknote.service.network.response.failure_reason.FailureReasonResponse
import ru.smartro.worknote.service.network.response.way_task.WayTaskResponse

class WayTaskViewModel(application: Application) : BaseViewModel(application) {

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

    fun getBreakDownTypes(): LiveData<Resource<BreakDownResponse>> {
        return network.getBreakDownTypes()
    }


    fun getFailReason(): LiveData<Resource<FailureReasonResponse>>{
        return network.getFailReason()
    }

    fun getCancelWayReason(): LiveData<Resource<CancelationReasonResponse>> {
        return network.getCancelWayReason()
    }

    fun insertWayTask(entity: WayTaskEntity) {
        db.insertWayTask(entity)
    }

    fun insertBreakDown(entities: List<ContainerBreakdownEntity>) {
        db.insertBreakDown(entities)
    }

    fun insertFailReason(entities: List<ContainerFailReasonEntity>) {
        db.insertFailReason(entities)
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        db.insertCancelWayReason(entities)
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return db.createObjectFromJson(clazz, json)
    }

    fun beginTransaction() {
        db.beginTransaction()
    }

    fun commitTransaction() {
        db.commitTransaction()
    }
/*
    fun insertWayTaskJson(entity: WayTaskJsonEntity) {
        db.insertWayTaskJson(entity)
    }

    fun insertWayPoint(entity: WayPointEntity) {
        db.insertWayPoint(entity)
    }*/

    fun progress(id: Int, body: ProgressBody): LiveData<Resource<EmptyResponse>> {
        return network.progress(id, body)
    }

}

