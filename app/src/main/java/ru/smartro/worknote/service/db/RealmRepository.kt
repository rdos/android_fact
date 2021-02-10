package ru.smartro.worknote.service.db

import android.content.Context
import io.realm.Realm
import io.realm.RealmModel
import ru.smartro.worknote.service.db.entity.container_service.ServedPointEntity
import ru.smartro.worknote.service.db.entity.way_task.WayTaskEntity

class RealmRepository(val context: Context) {
    private val realm = Realm.getDefaultInstance()

    fun insertWayTask(entity: WayTaskEntity) {
        realm.beginTransaction()
        realm.insertOrUpdate(entity)
        realm.commitTransaction()
    }

    fun findWayTask(): WayTaskEntity {
        return realm.where(WayTaskEntity::class.java).findFirst()!!
    }

    fun findLastId(any: Class<*>, fieldId: String): Int? {
        val currentId = realm.where(any as Class<RealmModel>).max(fieldId)?.toInt()
        return if (currentId == null) {
            1
        } else {
            currentId.toInt() + 1
        }
    }

    fun insertOrUpdateServedPoint(entity: ServedPointEntity) {
        realm.beginTransaction()
        realm.insertOrUpdate(entity)
        realm.commitTransaction()
    }

    fun findServedPointEntity(pointId: Int): ServedPointEntity {
        return realm.where(ServedPointEntity::class.java).equalTo("pId", pointId).findFirst()!!
    }

    fun beginTransaction() {
        realm.beginTransaction()
    }

    fun commitTransaction() {
        realm.commitTransaction()
    }

}