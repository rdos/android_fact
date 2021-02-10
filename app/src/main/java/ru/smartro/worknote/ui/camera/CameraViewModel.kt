package ru.smartro.worknote.ui.camera

import android.app.Application
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.db.entity.container_service.ServedPointEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findServedPointEntity(pointId: Int): ServedPointEntity {
        return db.findServedPointEntity(pointId)
    }

    fun beginTransaction() {
        db.beginTransaction()
    }

    fun commitTransaction() {
        db.commitTransaction()
    }
}

