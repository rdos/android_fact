package ru.smartro.worknote.service.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmModel
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.database.livedata.LiveRealmObject
import ru.smartro.worknote.util.PhotoTypeEnum

class RealmRepository(val context: Context) {
    private val realm = Realm.getDefaultInstance()

    fun insertWayTask(entity: WayTaskEntity) {
        realm.insertOrUpdate(entity)
    }

    fun updateContainerStatus(pointId: Int, containerId: Int, status : Int) {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        realm.beginTransaction()
        val pointEntity = wayTaskEntity.p!!.find { it.id == pointId }
        val containerEntity = pointEntity!!.cs!!.find { it.id == containerId }
        containerEntity!!.status = status
        realm.commitTransaction()
    }

    fun completePoint(pointId: Int) {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        realm.beginTransaction()
        wayTaskEntity.p?.find {it.id == pointId}?.isComplete = true
        realm.commitTransaction()
    }

    fun findWayTask(): WayTaskEntity {
        return realm.copyFromRealm(realm.where(WayTaskEntity::class.java).findFirst()!!)
    }

    fun findWayTaskLV(): LiveData<WayTaskEntity>? {
        val result = realm.where(WayTaskEntity::class.java).findFirst()
        return if (result != null) {
            LiveRealmObject<WayTaskEntity>(result)
        } else {
            null
        }
    }

    fun findLastId(any: Class<*>, fieldId: String): Int? {
        val currentId = realm.where(any as Class<RealmModel>).max(fieldId)?.toInt()
        return if (currentId == null) {
            1
        } else {
            currentId.toInt() + 1
        }
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return realm.copyFromRealm(realm.createObjectFromJson(clazz, json)!!)
    }

    fun insertOrUpdateServedPoint(entity: ServedPointEntity) {
        realm.beginTransaction()
        realm.insertOrUpdate(entity)
        realm.commitTransaction()
    }

    fun findServedPointEntityLV(pointId: Int): LiveData<ServedPointEntity>? {
        val result = realm.where(ServedPointEntity::class.java).equalTo("pId", pointId)
            .findFirstAsync()
        return if (result != null) {
            LiveRealmObject<ServedPointEntity>(result)
        } else {
            null
        }
    }

    fun findServedPointEntity(pointId: Int): ServedPointEntity? {
        return if (realm.where(ServedPointEntity::class.java).equalTo("pId", pointId)
                .findFirst() == null
        ) {
            null
        } else {
            realm.copyFromRealm(
                realm.where(ServedPointEntity::class.java).equalTo("pId", pointId).findFirst()!!
            )
        }
    }

    fun updatePhotoMediaOfServedPoint(isPhotoFor: Int, pointId: Int, photoPath: String) {
        realm.beginTransaction()
        val servedPointEntity = realm.where(ServedPointEntity::class.java).equalTo("pId", pointId)
            .findFirst()
        when (isPhotoFor) {
            PhotoTypeEnum.forAfterMedia -> {
                servedPointEntity?.mediaAfter?.add(photoPath)
            }
            PhotoTypeEnum.forBeforeMedia -> {
                servedPointEntity?.mediaBefore?.add(photoPath)
            }
        }
        realm.commitTransaction()
    }

    fun removePhotoFromServedEntity(isPhotoFor: Int, photoPath: String, wayPointId: Int) {
        realm.beginTransaction()
        val servedPointEntity = realm.where(ServedPointEntity::class.java)
            .equalTo("pId", wayPointId).findFirst()
        when (isPhotoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                servedPointEntity?.mediaBefore?.remove(photoPath)
                Log.d("REMOVE_PHOTO", "BEFORE")
            }
            PhotoTypeEnum.forAfterMedia -> {
                servedPointEntity?.mediaAfter?.remove(photoPath)
                Log.d("REMOVE_PHOTO", "AFTER")
            }
            PhotoTypeEnum.forProblemMedia -> {

            }
        }
        realm.commitTransaction()
    }

    fun beginTransaction() {
        realm.beginTransaction()
    }

    fun commitTransaction() {
        realm.commitTransaction()
    }
}