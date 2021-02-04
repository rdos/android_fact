package ru.smartro.worknote.ui.camera

import android.app.Application
import androidx.lifecycle.LiveData
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.service.db.entity.container_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoProblemEntity

class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun insertBeforePhoto(entity: PhotoBeforeEntity) {
        db.insertBeforePhoto(entity)
    }

    fun insertAfterPhoto(entity: PhotoAfterEntity) {
        db.insertAfterPhoto(entity)
    }

    fun findBeforePhoto(id: Int): LiveData<List<PhotoBeforeEntity>> {
        return db.findBeforePhotosById(id)
    }

    fun find1BeforePhotoById(id: Int): LiveData<PhotoBeforeEntity> {
        return db.find1BeforePhotoById(id)
    }

    suspend fun findBeforePhotosByIdNoLv(id: Int): List<PhotoBeforeEntity> {
        return db.findBeforePhotosByIdNoLv(id)
    }

    suspend fun findProblemPhotosByIdNoLv(id: Int): List<PhotoProblemEntity> {
        return db.findProblemPhotosByIdNoLv(id)
    }

    fun findAfterPhoto(id: Int): LiveData<List<PhotoAfterEntity>> {
        return db.findAfterPhotosById(id)
    }

    suspend fun findAfterPhotosByIdNoLv(id: Int): List<PhotoAfterEntity> {
        return db.findAfterPhotosByIdNoLv(id)
    }

    fun find1AfterPhotoById(id: Int): LiveData<PhotoBeforeEntity> {
        return db.find1BeforePhotoById(id)
    }

    fun delete1BeforePhoto(photoPath: String) {
        db.delete1BeforePhoto(photoPath)
    }

    fun delete1AfterPhoto(photoPath: String) {
        db.delete1AfterPhoto(photoPath)
    }

}

