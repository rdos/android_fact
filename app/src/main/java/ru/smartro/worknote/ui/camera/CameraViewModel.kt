package ru.smartro.worknote.ui.camera

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun findServedPointEntity(pointId: Int): ServedPointEntity? {
        return db.findServedPointEntity(pointId)
    }

    fun findServedPointEntityLV(pointId: Int): LiveData<ServedPointEntity>? {
        return db.findServedPointEntityLV(pointId)
    }

    fun updatePhotoMediaOfServedPoint(isPhotoFor: Int, pointId: Int, photoPath: String) {
        db.updatePhotoMediaOfServedPoint(isPhotoFor, pointId, photoPath)
    }

    fun removePhotoFromServedEntity(photoFor: Int, photoPath: String, wayPointId: Int) {
        db.removePhotoFromServedEntity(photoFor, photoPath, wayPointId)
    }

    fun beginTransaction() {
        db.beginTransaction()
    }

    fun commitTransaction() {
        db.commitTransaction()
    }
}

