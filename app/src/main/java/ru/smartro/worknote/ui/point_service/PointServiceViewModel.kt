package ru.smartro.worknote.ui.point_service

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.container_service.ServedContainerInfoEntity
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.service.network.response.served.ServedResponse

class PointServiceViewModel(application: Application) : BaseViewModel(application) {

    fun served(body: ServiceResultBody): LiveData<Resource<ServedResponse>> {
        return network.served(body)
    }

    fun updateContainerStatus(pointId: Int, containerId: Int, status: Int) {
        db.updateContainerStatus(pointId, containerId, status)
    }

    fun findContainerStatus(pointId: Int, containerId: Int): Int {
        return db.findContainerStatus(pointId, containerId)
    }

    fun pointHasBreakdown(pointId: Int): Boolean {
        return db.pointHasBreakdown(pointId)
    }

    fun currentContainerStatus(pointId: Int, containerId: Int): Int {
        return db.currentContainerStatus(pointId, containerId)
    }

    fun addServedContainerInfo(container: ServedContainerInfoEntity, wayPointId: Int) {
        db.addServedContainerInfo(container, wayPointId)
    }

    fun updatePointStatus(pointId: Int, status: Int) {
        db.updatePointStatus(pointId, status)
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findLastId(any: Class<*>, fieldId: String): Int? {
        return db.findLastId(any, fieldId)
    }

    fun insertOrUpdateServedPoint(entity: ServedPointEntity) {
        db.insertOrUpdateServedPoint(entity)
    }

    fun findServedPointEntity(pointId: Int): ServedPointEntity? {
        return db.findServedPointEntity(pointId)
    }

    fun beginTransaction() {
        db.beginTransaction()
    }

    fun commitTransaction() {
        db.commitTransaction()
    }

    fun removePhotoFromServedEntity(photoFor: Int, photoPath: String, wayPointId: Int) {
        db.removePhotoFromServedEntity(photoFor, photoPath, wayPointId)
    }

}

