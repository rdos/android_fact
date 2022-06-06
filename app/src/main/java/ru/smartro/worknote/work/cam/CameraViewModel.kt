package ru.smartro.worknote.work.cam

import android.app.Application
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.work.PlatformEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return baseDat.findPlatformEntity(platformId)
    }

    fun findContainerEntity(containerId: Int) =
        baseDat.findContainerEntity(containerId)

}

