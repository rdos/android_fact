package ru.smartro.worknote.ui.platform_service

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.database.entity.work_order.WayTaskEntity

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

    fun updatePlatformStatus(platformId: Int, status: String) {
        db.updatePlatformStatus(platformId, status)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return db.findPlatformEntity(platformId)
    }

    fun findContainerEntity(containerId: Int): ContainerEntity {
        return db.findContainerEntity(containerId)
    }

    fun removePlatformMedia(photoFor: Int, photoPath: String, platformId: Int) {
        db.removePlatformMedia(photoFor, photoPath, platformId)
    }

    fun removeContainerMedia (containerId : Int, imageBase64 : String){
        db.removeContainerMedia(containerId, imageBase64)
    }
}

