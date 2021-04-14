package ru.smartro.worknote.ui.platform_service

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity

class PlatformServiceViewModel(application: Application) : BaseViewModel(application) {

    fun updateContainerVolume(containerId: Int, volume: Double, comment: String) {
        db.updateContainerVolume(containerId, volume, comment)
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findLastId(any: Class<*>, fieldId: String): Int? {
        return db.findLastId(any, fieldId)
    }

    fun updatePlatformStatus(platformId: Int, status: Int) {
        db.updatePlatformStatus(platformId, status)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity? {
        return db.findPlatformEntity(platformId)
    }

    fun removePhotoFromServedEntity(photoFor: Int, photoPath: String, wayPointId: Int) {
        db.removeMediaPlatformEntity(photoFor, photoPath, wayPointId)
    }

}

