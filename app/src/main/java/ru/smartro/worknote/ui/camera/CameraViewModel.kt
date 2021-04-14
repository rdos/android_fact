package ru.smartro.worknote.ui.camera

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findPlatformEntity(pointId: Int): PlatformEntity? {
        return db.findPlatformEntity(pointId)
    }

    fun updateMediaPlatform(isPhotoFor: Int, pointId: Int, photoPath: String) {
        db.updateMediaPlatform(isPhotoFor, pointId, photoPath)
    }

}

