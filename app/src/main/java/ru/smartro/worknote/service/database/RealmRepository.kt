package ru.smartro.worknote.service.database

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.database.entity.work_order.*
import ru.smartro.worknote.service.network.response.work_order.*
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum
import ru.smartro.worknote.util.StatusEnum

class RealmRepository(private val realm: Realm) {

    fun insertWayTask(response: Workorder) {

        fun mapMedia(data: List<String>): RealmList<String> {
            return data.mapTo(RealmList()) { it }
        }

        fun mapContainers(list: List<Container>): RealmList<ContainerEntity> {
            return list.mapTo(RealmList()) {
                ContainerEntity(
                    client = it.client, contacts = it.contacts, failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, containerId = it.id, isActiveToday = it.isActiveToday,
                    number = it.number, status = it.status, typeId = it.typeId, volume = it.volume
                )
            }
        }

        fun mapPlatforms(data: List<Platform>): RealmList<PlatformEntity> {
            return data.mapTo(RealmList()) {
                PlatformEntity(
                    address = it.address, afterMedia = mapMedia(it.afterMedia),
                    beforeMedia = mapMedia(it.beforeMedia), beginnedAt = it.beginnedAt, containers = mapContainers(it.containers),
                    coords = RealmList(it.coords[0], it.coords[1]), failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, finishedAt = it.finishedAt, platformId = it.id,
                    name = it.name, srpId = it.srpId, status = StatusEnum.NEW
                )
            }
        }

        fun mapStart(data: Start) = StartEntity(
            coords = RealmList(data.coords[0], data.coords[1]), name = data.name, id = data.id
        )

        fun mapUnload(data: Unload) = UnloadEntity(
            coords = RealmList(data.coords[0], data.coords[1]), name = data.name, id = data.id
        )

        val wayTask = WayTaskEntity(
            id = response.id, accounting = response.accounting,
            beginnedAt = response.beginnedAt, finishedAt = response.finishedAt,
            name = response.name, platforms = mapPlatforms(response.platforms),
            start = mapStart(response.start), unload = mapUnload(response.unload)
        )

        realm.beginTransaction()
        realm.insertOrUpdate(wayTask)
        realm.commitTransaction()
    }

    fun clearData() {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    fun insertBreakDown(entities: List<BreakDownEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }

    fun insertFailReason(entities: List<FailReasonEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        realm.beginTransaction()
        realm.insertOrUpdate(entities)
        realm.commitTransaction()
    }


    fun findBreakdownByValue(problem: String): BreakDownEntity {
        return realm.copyFromRealm(
            realm.where(BreakDownEntity::class.java).equalTo("problem", problem).findFirst()!!
        )
    }

    fun findFailReasonByValue(problem: String): FailReasonEntity {
        return realm.copyFromRealm(
            realm.where(FailReasonEntity::class.java).equalTo("problem", problem).findFirst()!!
        )
    }

    fun findAllFailReason(): List<String> {
        val found = realm.copyFromRealm(realm.where(FailReasonEntity::class.java).findAll())
        return found.map { it.problem!! }
    }

    fun findAllBreakDown(): List<String> {
        val found = realm.copyFromRealm(realm.where(BreakDownEntity::class.java).findAll())
        return found.map { it.problem!! }
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return realm.copyFromRealm(realm.where(CancelWayReasonEntity::class.java).findAll())
    }

    /** добавление заполненности контейнера **/
    fun updateContainerVolume(containerId: Int, volume: Double, comment: String) {
        val container = realm.where(ContainerEntity::class.java).equalTo("containerId", containerId)
            .findFirst()!!
        realm.beginTransaction()
        container.volume = volume
        container.comment = comment
        if (container.status == StatusEnum.NEW) {
            container.status = StatusEnum.SUCCESS
        }
        updateTimer()
        realm.commitTransaction()
    }

    fun updateContainerProblem(platformId: Int, containerId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        val container = realm.where(ContainerEntity::class.java).equalTo("containerId", containerId)
            .findFirst()!!
        val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
            .findFirst()!!
        realm.beginTransaction()
        when (problemType) {
            ProblemEnum.BREAKDOWN -> {
                val problemId = findBreakdownByValue(problem).id
                container.breakdownReasonId = problemId
                container.status = StatusEnum.ERROR
            }
            ProblemEnum.ERROR -> {
                val problemId = findFailReasonByValue(problem).id
                container.failureReasonId = problemId
                platform.status = StatusEnum.ERROR
                platform.containers.forEach { it.status = StatusEnum.ERROR }
            }
            ProblemEnum.BOTH -> {
                val breakdownProblemId = findBreakdownByValue(problem).id
                val failReasonProblemId = findFailReasonByValue(failProblem!!).id
                container.breakdownReasonId = breakdownProblemId
                container.failureReasonId = failReasonProblemId
                platform.status = StatusEnum.ERROR
                platform.containers.forEach { it.status = StatusEnum.ERROR }

            }
        }
        container.failureComment = problemComment
        updateTimer()
        realm.commitTransaction()
    }

    fun updatePlatformProblem(platformId: Int, failureComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
            .findFirst()!!
        realm.beginTransaction()
        when (problemType) {
            ProblemEnum.ERROR -> {
                val problemId = findFailReasonByValue(problem).id
                platform.failureReasonId = problemId
                platform.status = StatusEnum.ERROR
                platform.containers.forEach { it.status = StatusEnum.ERROR }
            }
            ProblemEnum.BOTH -> {
                val failReasonProblemId = findFailReasonByValue(failProblem!!).id
                platform.failureReasonId = failReasonProblemId
                platform.status = StatusEnum.ERROR
                platform.containers.forEach { it.status = StatusEnum.ERROR }
            }
        }
        platform.failureComment = failureComment
        updateTimer()
        realm.commitTransaction()
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
            .findFirst()!!
        realm.beginTransaction()
        updateTimer()
        if (platform.status == StatusEnum.NEW) {
            platform.status = status
        }
        realm.commitTransaction()
    }

    fun updateContainerStatus(containerId: Int, status: String) {
        val container = realm.where(ContainerEntity::class.java).equalTo("containerId", containerId)
            .findFirst()!!
        realm.beginTransaction()
        updateTimer()
        if (container.status == StatusEnum.NEW) container.status = status
        realm.commitTransaction()
    }

    fun findWayTask(): WayTaskEntity {
        return realm.copyFromRealm(realm.where(WayTaskEntity::class.java).findFirst()!!)
    }

    fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
        val wayTask = findWayTask()
        return wayTask.platforms.find { it.coords[0] == lat && it.coords[1] == lon }!!
    }

    fun findWayTaskLV(): WayTaskEntity {
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

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return realm.copyFromRealm(realm.createObjectFromJson(clazz, json)!!)
    }

    fun findPlatformEntity(platformId: Int) =
        realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()!!

    fun findContainerEntity(containerId: Int) =
        realm.where(ContainerEntity::class.java)
            .equalTo("containerId", containerId)
            .findFirst()!!

    /** добавление фото в платформу **/

    fun updatePlatformMedia(imageFor: Int, platformId: Int, imageBase64: String) {
        realm.beginTransaction()
        val platformEntity = realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()
        updateTimer()
        when (imageFor) {
            PhotoTypeEnum.forAfterMedia -> {
                platformEntity?.afterMedia?.add(imageBase64)
            }
            PhotoTypeEnum.forBeforeMedia -> {
                platformEntity?.beforeMedia?.add(imageBase64)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                platformEntity?.failureMedia?.add(imageBase64)
            }
        }
        realm.commitTransaction()
    }

    fun updateContainerMedia(containerId: Int, imageBase64: String) {
        val containerEntity = realm.where(ContainerEntity::class.java)
            .equalTo("containerId", containerId)
            .findFirst()!!
        realm.beginTransaction()
        containerEntity.failureMedia.add(imageBase64)
        updateTimer()
        realm.commitTransaction()
    }

    fun removeContainerMedia(containerId: Int, imageBase64: String) {
        val containerEntity = realm.where(ContainerEntity::class.java)
            .equalTo("containerId", containerId)
            .findFirst()!!
        realm.beginTransaction()
        containerEntity.failureMedia.remove(imageBase64)
        realm.commitTransaction()
    }

    /** удалить фото с платформы **/
    fun removePlatformMedia(imageFor: Int, imageBase64: String, platformId: Int) {
        realm.beginTransaction()
        val platformEntity = realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId).findFirst()
        updateTimer()
        when (imageFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                platformEntity?.beforeMedia?.remove(imageBase64)
            }
            PhotoTypeEnum.forAfterMedia -> {
                platformEntity?.afterMedia?.remove(imageBase64)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                platformEntity?.failureMedia?.remove(imageBase64)
            }
        }
        realm.commitTransaction()
    }

    private fun updateTimer() {
        AppPreferences.lastUpdateTime = MyUtil.timeStamp()
    }

}