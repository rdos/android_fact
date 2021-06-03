package ru.smartro.worknote.ui.platform_service

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity

class PlatformServiceViewModel(application: Application) : BaseViewModel(application) {

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double, comment: String) {
        db.updateContainerVolume(platformId, containerId, volume, comment)
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

    fun removeContainerMedia (platformId: Int, containerId : Int, imageBase64 : String){
        db.removeContainerMedia(platformId, containerId, imageBase64)
    }
    fun updatePlatformKGO (platformId: Int, kgoVolume : Int){
        db.updatePlatformKGO(platformId, kgoVolume)
    }
}

