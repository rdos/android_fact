package ru.smartro.worknote.ui.map

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.WayTaskBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.way_task.WayTaskResponse

class MapViewModel(application: Application) : BaseViewModel(application) {

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

    fun completeWay(id : Int, completeWayBody: CompleteWayBody) : LiveData<Resource<EmptyResponse>>{
        return network.completeWay(id, completeWayBody)
    }

    fun createServedPointEntityIfNull(wayPoint: WayPointEntity) {
        db.createServedPointEntityIfNull(wayPoint)
    }

    fun earlyComplete(id : Int, body : EarlyCompleteBody) : LiveData<Resource<EmptyResponse>>{
        return network.earlyComplete(id, body)
    }

    fun clearData(){
        return db.clearData()
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return db.findCancelWayReason()
    }

}

