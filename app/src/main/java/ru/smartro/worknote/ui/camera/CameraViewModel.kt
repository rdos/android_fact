package ru.smartro.worknote.ui.camera

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return db.findPlatformEntity(platformId)
    }

    fun updatePlatformMedia(imageFor: Int, pointId: Int, imageBase64: String) {
        db.updatePlatformMedia(imageFor, pointId, imageBase64)
    }

    fun updateContainerMedia(platformId: Int, containerId: Int, imageBase64: String) {
        db.updateContainerMedia(platformId, containerId, imageBase64)
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        db.updatePlatformStatus(platformId, status)
    }

    fun findContainerEntity(containerId: Int) =
        db.findContainerEntity(containerId)

}

