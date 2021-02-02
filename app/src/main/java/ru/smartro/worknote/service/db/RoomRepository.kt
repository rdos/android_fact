package ru.smartro.worknote.service.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.smartro.worknote.service.db.entity.co_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.co_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity
import java.io.File

class RoomRepository(private val dao: RoomDao) {

    fun insertWayTaskJson(entity: WayTaskJsonEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            return@launch dao.insertWayTaskJson(entity)
        }
    }

    fun insertBeforePhoto(entity: PhotoBeforeEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            return@launch dao.insertPhotoBefore(entity)
        }
    }

    fun insertWayPoint(entity: WayPointEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            return@launch dao.insertWayPoint(entity)
        }
    }

    fun insertAfterPhoto(entity: PhotoAfterEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            return@launch dao.insertPhotoAfter(entity)
        }
    }

    fun insertContainer(entity: ContainerInfoEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            return@launch dao.insertContainer(entity)
        }
    }

    fun findWayTaskJsonByUser(userLogin: String): LiveData<WayTaskJsonEntity> {
        return dao.findWayTaskJsonByUser(userLogin)
    }

    fun findContainerInfoByPointId(wayPointId: Int): LiveData<List<ContainerInfoEntity>> {
        return dao.findContainerInfoByPointId(wayPointId)
    }

    fun findContainerInfo(): LiveData<List<ContainerInfoEntity>> {
        return dao.findContainerInfo()
    }

    fun findBeforePhotosById(id: Int): LiveData<List<PhotoBeforeEntity>> {
        return dao.findBeforePhotosById(id)
    }

    fun find1BeforePhotoById(id: Int): LiveData<PhotoBeforeEntity> {
        return dao.find1BeforePhotoById(id)
    }

    fun delete1BeforePhoto(photoPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(photoPath)
            dao.delete1BeforePhoto(photoPath)
            file.delete()
        }
    }

    fun delete1AfterPhoto(photoPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(photoPath)
            dao.delete1AfterPhoto(photoPath)
            file.delete()
        }
    }

    fun findAfterPhotosById(id: Int): LiveData<List<PhotoAfterEntity>> {
        return dao.findAfterPhotosById(id)
    }

    fun find1AfterPhotoById(id: Int): LiveData<PhotoAfterEntity> {
        return dao.find1AfterPhotoById(id)
    }
}