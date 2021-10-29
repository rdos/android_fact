package ru.smartro.worknote.ui.platform_serve

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.ImageEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity

class PlatformServeViewModel(application: Application) : BaseViewModel(application) {

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?, comment: String?) {
        db.updateContainerVolume(platformId, containerId, volume, comment)
    }

    fun clearContainerVolume(platformId: Int, containerId: Int) {
        db.clearContainerVolume(platformId, containerId)
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

    fun findAllContainerInPlatform(platformId: Int): List<ContainerEntity> {
        return db.findAllContainerInPlatform(platformId)
    }

    fun removePlatformMedia(photoFor: Int, image: ImageEntity, platformId: Int) {
        db.removePlatformMedia(photoFor, image, platformId)
    }

    fun removeContainerMedia(platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        db.removeContainerMedia(platformId, containerId, imageBase64)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: Double, isTakeaway: Boolean) {
        db.updatePlatformKGO(platformId, kgoVolume, isTakeaway)
    }
}