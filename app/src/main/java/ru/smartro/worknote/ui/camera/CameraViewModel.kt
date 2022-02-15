package ru.smartro.worknote.ui.camera

import android.app.Application
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.work.PlatformEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return baseDat.findPlatformEntity(platformId)
    }

    fun updatePlatformMedia(
        imageFor: Int, pointId: Int, imageBase64: String,
        coords: Point,
        currentCoordinateAccuracy: String
    ) {
        baseDat.updatePlatformMedia(imageFor, pointId, imageBase64, coords, currentCoordinateAccuracy)
    }
//    fun updateImageImageEntity(md5: String, readBytes: ByteArray) {
//        baseDat.updateImageImageEntity(md5, readBytes)
//    }
    fun updateContainerMedia(
    platformId: Int, containerId: Int, imageBase64: String,
    coords: Point,
    currentCoordinateAccuracy: String
) {
        baseDat.updateContainerMedia(platformId, containerId, imageBase64, coords,
            currentCoordinateAccuracy)
    }

    fun findContainerEntity(containerId: Int) =
        baseDat.findContainerEntity(containerId)

}

