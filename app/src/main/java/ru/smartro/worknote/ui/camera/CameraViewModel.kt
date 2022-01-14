package ru.smartro.worknote.ui.camera

import android.app.Application
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.work.PlatformEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return db.findPlatformEntity(platformId)
    }

    fun updatePlatformMedia(imageFor: Int, pointId: Int, imageBase64: String, coords : Point) {
        db.updatePlatformMedia(imageFor, pointId, imageBase64, coords)
    }

    fun updateContainerMedia(platformId: Int, containerId: Int, imageBase64: String, coords : Point) {
        db.updateContainerMedia(platformId, containerId, imageBase64, coords)
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        db.updatePlatformStatus(platformId, status)
    }

    fun findContainerEntity(containerId: Int) =
        db.findContainerEntity(containerId)

}

