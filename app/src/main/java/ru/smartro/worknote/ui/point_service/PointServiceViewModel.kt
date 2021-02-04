package ru.smartro.worknote.ui.point_service

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.Resource
import ru.smartro.worknote.service.body.served.ServiceResultBody
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoProblemEntity
import ru.smartro.worknote.service.response.served.ServedResponse

class PointServiceViewModel(application: Application) : BaseViewModel(application) {

    fun served(body : ServiceResultBody): LiveData<Resource<ServedResponse>> {
        return network.served(body)
    }

    fun insertBeforePhoto(entity: PhotoBeforeEntity) {
        db.insertBeforePhoto(entity)
    }

    fun insertWayPoint(entity: WayPointEntity) {
        db.insertWayPoint(entity)
    }

    fun findBeforePhoto(id: Int): LiveData<List<PhotoBeforeEntity>> {
        return db.findBeforePhotosById(id)
    }

    suspend fun findBeforePhotosByIdNoLv(id: Int): List<PhotoBeforeEntity> {
        return db.findBeforePhotosByIdNoLv(id)
    }

    suspend fun findProblemPhotosByIdNoLv(id: Int): List<PhotoProblemEntity> {
        return db.findProblemPhotosByIdNoLv(id)
    }

    suspend fun findAfterPhotosByIdNoLv(id: Int): List<PhotoAfterEntity> {
        return db.findAfterPhotosByIdNoLv(id)
    }

    fun find1BeforePhotoById(id: Int): LiveData<PhotoBeforeEntity> {
        return db.find1BeforePhotoById(id)
    }

    fun findContainerInfo(): LiveData<List<ContainerInfoEntity>> {
        return db.findContainerInfo()
    }

    suspend fun findContainerInfoNOLV(): List<ContainerInfoEntity> {
        return db.findContainerInfoNOLV()
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

