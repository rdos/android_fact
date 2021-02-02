package ru.smartro.worknote.ui.map

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.body.WayTaskBody
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity
import ru.smartro.worknote.service.response.way_task.WayTaskResponse

class MapViewModel(application: Application) : BaseViewModel(application) {

    fun getWayTask(wayId: Int, wayTaskBody: WayTaskBody): LiveData<Resource<WayTaskResponse>> {
        return network.getWayTask(wayId, wayTaskBody)
    }

    fun findWayTaskJsonByUser(userLogin : String): LiveData<WayTaskJsonEntity> {
        return db.findWayTaskJsonByUser(userLogin)
    }

    fun findContainerInfoByPointId(wayPointId: Int): LiveData<List<ContainerInfoEntity>> {
        return db.findContainerInfoByPointId(wayPointId)
    }

    fun findContainerInfo(): LiveData<List<ContainerInfoEntity>> {
        return db.findContainerInfo()
    }

}

