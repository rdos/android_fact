package ru.smartro.worknote.service.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.smartro.worknote.service.db.entity.container_info.ContainerInfoEntity
import ru.smartro.worknote.service.db.entity.container_info.WayPointEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoAfterEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoBeforeEntity
import ru.smartro.worknote.service.db.entity.container_service.PhotoProblemEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskJsonEntity

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWayTaskJson(entity: WayTaskJsonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoBefore(entity: PhotoBeforeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWayPoint(entity: WayPointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoAfter(entity: PhotoAfterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContainer(entity: ContainerInfoEntity)

    @Query("SELECT * FROM ContainerInfoEntity")
    fun findContainerInfo(): LiveData<List<ContainerInfoEntity>>

    @Query("SELECT * FROM ContainerInfoEntity")
    suspend fun findContainerInfoNOLV(): List<ContainerInfoEntity>

    @Query("SELECT * FROM WayTaskJsonEntity WHERE userLogin =:userLogin")
    fun findWayTaskJsonByUser(userLogin: String): LiveData<WayTaskJsonEntity>

    @Query("SELECT * FROM ContainerInfoEntity WHERE wayPointId =:wayPointId")
    fun findContainerInfoByPointId(wayPointId: Int): LiveData<List<ContainerInfoEntity>>

    @Query("SELECT * FROM PhotoBeforeEntity WHERE pointID =:id")
    fun findBeforePhotosById(id: Int): LiveData<List<PhotoBeforeEntity>>

    @Query("SELECT * FROM PhotoBeforeEntity WHERE id =:id")
    fun find1BeforePhotoById(id: Int): LiveData<PhotoBeforeEntity>

    @Query("DELETE FROM PhotoBeforeEntity WHERE photoPath =:photoPath")
    fun delete1BeforePhoto(photoPath: String)

    @Query("DELETE FROM PhotoAfterEntity WHERE photoPath =:photoPath")
    fun delete1AfterPhoto(photoPath: String)

    @Query("DELETE FROM PhotoBeforeEntity WHERE photoPath =:photoPath")
    fun delete1ProblemPhoto(photoPath: String)

    @Query("SELECT * FROM PhotoAfterEntity WHERE pointID =:id")
    fun findAfterPhotosById(id: Int): LiveData<List<PhotoAfterEntity>>

    @Query("SELECT * FROM PhotoAfterEntity WHERE pointID =:id")
    suspend fun findAfterPhotosByIdNoLv(id: Int): List<PhotoAfterEntity>

    @Query("SELECT * FROM PhotoBeforeEntity WHERE pointID =:id")
    suspend fun findBeforePhotosByIdNoLv(id: Int): List<PhotoBeforeEntity>

    @Query("SELECT * FROM PhotoBeforeEntity WHERE pointID =:id")
    suspend fun findProblemPhotosByIdNoLv(id: Int): List<PhotoProblemEntity>

    @Query("SELECT * FROM PhotoAfterEntity WHERE id =:id")
    fun find1AfterPhotoById(id: Int): LiveData<PhotoAfterEntity>

}