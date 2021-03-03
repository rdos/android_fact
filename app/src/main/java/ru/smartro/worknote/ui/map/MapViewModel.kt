package ru.smartro.worknote.ui.map

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.WayTaskBody
import ru.smartro.worknote.service.network.response.way_task.WayTaskResponse

class MapViewModel(application: Application) : BaseViewModel(application) {

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findWayTaskLV(): LiveData<WayTaskEntity>? {
        return db.findWayTaskLV()
    }

}

