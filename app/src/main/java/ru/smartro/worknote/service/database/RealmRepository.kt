package ru.smartro.worknote.service.database

import android.content.Context
import android.util.Log
import io.realm.Realm
import io.realm.RealmModel
import ru.smartro.worknote.service.database.entity.container_service.ServedContainerInfoEntity
import ru.smartro.worknote.service.database.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerBreakdownEntity
import ru.smartro.worknote.service.database.entity.problem.ContainerFailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum

class RealmRepository(val context: Context) {
    private val realm = Realm.getDefaultInstance()

    fun insertWayTask(entity: WayTaskEntity) {
        realm.insertOrUpdate(entity)
    }

    fun clearData() {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    fun insertBreakDown(entities: List<ContainerBreakdownEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }

    fun insertFailReason(entities: List<ContainerFailReasonEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }

    fun findBreakDown(): List<ContainerBreakdownEntity> {
        return realm.copyFromRealm(realm.where(ContainerBreakdownEntity::class.java).findAll())
    }

    fun findFailReason(): List<ContainerFailReasonEntity> {
        return realm.copyFromRealm(realm.where(ContainerFailReasonEntity::class.java).findAll())
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return realm.copyFromRealm(realm.where(CancelWayReasonEntity::class.java).findAll())
    }

    fun updateContainerStatus(pointId: Int, containerId: Int, status: Int) {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        realm.beginTransaction()
        val pointEntity = wayTaskEntity.p!!.find { it.id == pointId }
        val containerEntity = pointEntity!!.cs!!.find { it.id == containerId }
        containerEntity!!.status = status
        realm.commitTransaction()
    }

    fun findContainerStatus(pointId: Int, containerId: Int): Int {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        val pointEntity = wayTaskEntity.p!!.find { it.id == pointId }
        val containerEntity = pointEntity!!.cs!!.find { it.id == containerId }
        return containerEntity!!.status
    }

    fun pointHasBreakdown(pointId: Int): Boolean {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        val pointEntity = wayTaskEntity.p!!.find { it.id == pointId }
        return pointEntity!!.cs!!.any { it.status == StatusEnum.breakDown }
    }

    fun currentContainerStatus(pointId: Int, containerId: Int): Int {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        val pointEntity = wayTaskEntity.p!!.find { it.id == pointId }
        return pointEntity!!.cs!!.find { it.id == containerId }!!.status
    }

    fun addServedContainerInfo(container: ServedContainerInfoEntity, wayPointId: Int) {
        val entity = realm.where(ServedPointEntity::class.java).equalTo("pId", wayPointId)
            .findFirst()
        realm.beginTransaction()
        entity?.cs!!.add(container)
        realm.commitTransaction()
    }

    fun updatePointStatus(pointId: Int, status: Int) {
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()!!
        realm.beginTransaction()
        wayTaskEntity.p?.find { it.id == pointId }?.status = status
        realm.commitTransaction()
    }

    fun findWayTask(): WayTaskEntity {
        return realm.copyFromRealm(realm.where(WayTaskEntity::class.java).findFirst()!!)
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

    fun findServedPointEntity(pointId: Int): ServedPointEntity? {
        return if (realm.where(ServedPointEntity::class.java).equalTo("pId", pointId).findFirst() == null
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
            PhotoTypeEnum.forProblemPoint -> {
                servedPointEntity?.mediaPointProblem?.add(photoPath)
            }
            PhotoTypeEnum.forProblemContainer -> {
                servedPointEntity?.mediaProblemContainer?.add(photoPath)
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
            PhotoTypeEnum.forProblemPoint -> {
                servedPointEntity?.mediaPointProblem?.remove(photoPath)
                Log.d("REMOVE_PHOTO", "AFTER")
            }
            PhotoTypeEnum.forProblemContainer -> {
                servedPointEntity?.mediaProblemContainer?.remove(photoPath)
                Log.d("REMOVE_PHOTO", "AFTER")
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