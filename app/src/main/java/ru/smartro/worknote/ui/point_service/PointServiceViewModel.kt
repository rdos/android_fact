package ru.smartro.worknote.ui.point_service

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.db.entity.co_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity

class PointServiceViewModel(application: Application) : BaseViewModel(application) {

    fun insertBeforePhoto(entity: PhotoBeforeEntity) {
        db.insertBeforePhoto(entity)
    }

    fun insertWayPoint(entity: WayPointEntity) {
        db.insertWayPoint(entity)
    }

    fun findBeforePhoto(id: Int): LiveData<List<PhotoBeforeEntity>> {
        return db.findBeforePhotosById(id)
    }

    fun find1BeforePhotoById(id: Int): LiveData<PhotoBeforeEntity> {
        return db.find1BeforePhotoById(id)
    }

    fun findContainerInfo(): LiveData<List<ContainerInfoEntity>> {
        return db.findContainerInfo()
    }

    fun delete1BeforePhoto(photoPath: String) {
        db.delete1BeforePhoto(photoPath)
    }

    fun delete1AfterPhoto(photoPath: String) {
        db.delete1AfterPhoto(photoPath)
    }

    fun insertContainer(entity: ContainerInfoEntity) {
        db.insertContainer(entity)
    }


}

