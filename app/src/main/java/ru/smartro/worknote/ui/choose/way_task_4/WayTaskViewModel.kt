package ru.smartro.worknote.ui.choose.way_task_4

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.body.ProgressBody
import ru.smartro.worknote.service.body.WayTaskBody
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity
import ru.smartro.worknote.service.response.EmptyResponse
import ru.smartro.worknote.service.response.way_task.WayTaskResponse

class WayTaskViewModel(application: Application) : BaseViewModel(application) {

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

    fun insertWayTaskJson(entity: WayTaskJsonEntity) {
        db.insertWayTaskJson(entity)
    }

    fun insertWayPoint(entity: WayPointEntity) {
        db.insertWayPoint(entity)
    }

    fun progress(id: Int, body: ProgressBody): LiveData<Resource<EmptyResponse>> {
        return network.progress(id, body)
    }

}

