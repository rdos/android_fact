package ru.smartro.worknote.work.platform_serve

import android.app.Application
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity

class PlatformServeViewModel(application: Application) : BaseViewModel(application) {

    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        baseDat.updateContainerVolume(platformId, containerId, volume)
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        baseDat.updateContainerComment(platformId, containerId, comment)
    }

    fun updateSelectionVolume(platformId: Int, volume: Double?) {
        baseDat.updateSelectionVolume(platformId, volume)
    }

    fun clearContainerVolume(platformId: Int, containerId: Int) {
        baseDat.clearContainerVolume(platformId, containerId)
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        baseDat.updatePlatformStatusSuccess(platformId)
    }

    fun updatePlatformStatusUnfinished(platformId: Int) {
        baseDat.updatePlatformStatusUnfinished(platformId)
    }

    fun findContainerEntity(containerId: Int): ContainerEntity {
        return baseDat.findContainerEntity(containerId)
    }

    fun removePlatformMedia(photoFor: Int, image: ImageEntity, platformId: Int) {
        baseDat.removePlatformMedia(photoFor, image, platformId)
    }

    fun removeContainerMedia(photoFor: Int,platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        baseDat.removeContainerMedia(photoFor, platformId, containerId, imageBase64)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
    }


}