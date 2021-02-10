package ru.smartro.worknote.ui.point_service

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.db.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.service.network.response.served.ServedResponse

class PointServiceViewModel(application: Application) : BaseViewModel(application) {

    fun served(body: ServiceResultBody): LiveData<Resource<ServedResponse>> {
        return network.served(body)
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

    fun findServedPointEntity(pointId: Int): ServedPointEntity {
        return db.findServedPointEntity(pointId)
    }

    fun beginTransaction() {
        db.beginTransaction()
    }

    fun commitTransaction() {
        db.commitTransaction()
    }

}

