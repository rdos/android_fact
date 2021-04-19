package ru.smartro.worknote.service.database

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.service.database.entity.way_task.*
import ru.smartro.worknote.service.network.response.way_task.ContainerInfo
import ru.smartro.worknote.service.network.response.way_task.WayInfo
import ru.smartro.worknote.service.network.response.way_task.WayStartPoint
import ru.smartro.worknote.service.network.response.way_task.WayUnloadPoint
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.ProblemEnum
import ru.smartro.worknote.util.StatusEnum

class RealmRepository(private val realm: Realm) {

    fun insertWayTask(response: WayInfo) {
        fun mapContainers(list: List<ContainerInfo>): RealmList<ContainerEntity> {
            return list.mapTo(RealmList()) {
                ContainerEntity(
                    containerId = it.id, client = it.client, contacts = it.contacts, isActive = it.isActive,
                    number = it.number, type_id = it.typeId, comment = null, volume = null, breakdownReasonId = null,
                    failureReasonId = null, problemComment = null, status = StatusEnum.EMPTY
                )
            }
        }

        val platforms = response.platforms.mapTo(RealmList()) {
            PlatformEntity(
                platformId = it.id, address = it.address, lat = it.coordinate[0], lon = it.coordinate[1],
                name = it.name, containers = mapContainers(it.containers), srpId = it.srpId, status = StatusEnum.EMPTY, mediaAfter = RealmList(),
                mediaBefore = RealmList(), mediaPlatformProblem = RealmList(), mediaContainerProblem = RealmList(), breakdownReasonId = null,
                failureReasonId = null, problemComment = null, updatedAt = null, organisationId = AppPreferences.organisationId, woId = AppPreferences.wayListId
            )
        }

        fun mapStart(data: WayStartPoint) = StartPlatformEntity(
            lat = data.coordinates[0], lon = data.coordinates[0], name = data.name
        )

        fun mapUnload(data: WayUnloadPoint) = UnloadPlatformEntity(
            lat = data.coordinates[0], lon = data.coordinates[1], name = data.name
        )

        val wayTask = WayTaskEntity(
            id = response.id, accounting = response.accounting, begunAt = response.beginnedAt, finishedAt = response.finishedAt,
            name = response.name, platfroms = platforms, start = mapStart(response.start), unload = mapUnload(response.unload),
            updatedAt = MyUtil.timeStamp()
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

    fun findAllBreakDown(): List<BreakDownEntity> {
        return realm.copyFromRealm(realm.where(BreakDownEntity::class.java).findAll())
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

    fun findAllFailReason(): List<FailReasonEntity> {
        return realm.copyFromRealm(realm.where(FailReasonEntity::class.java).findAll())
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
        if (container.status == StatusEnum.EMPTY) {
            container.status = StatusEnum.COMPLETED
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
                container.status = StatusEnum.BREAKDOWN
            }
            ProblemEnum.FAILURE -> {
                val problemId = findFailReasonByValue(problem).id
                container.failureReasonId = problemId
                platform.status = StatusEnum.FAILURE
                platform.containers?.forEach { it.status = StatusEnum.FAILURE }
            }
            ProblemEnum.BOTH -> {
                val breakdownProblemId = findBreakdownByValue(problem).id
                val failReasonProblemId = findFailReasonByValue(failProblem!!).id
                container.breakdownReasonId = breakdownProblemId
                container.failureReasonId = failReasonProblemId
                platform.status = StatusEnum.FAILURE
                platform.containers?.forEach { it.status = StatusEnum.FAILURE }

            }
        }
        container.problemComment = problemComment
        updateTimer()
        realm.commitTransaction()
    }

    fun updatePlatformProblem(platformId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
            .findFirst()!!
        realm.beginTransaction()
        when (problemType) {
            ProblemEnum.BREAKDOWN -> {
                val problemId = findBreakdownByValue(problem).id
                platform.breakdownReasonId = problemId
                if (platform.status == StatusEnum.EMPTY) platform.status = StatusEnum.BREAKDOWN
            }
            ProblemEnum.FAILURE -> {
                val problemId = findFailReasonByValue(problem).id
                platform.failureReasonId = problemId
                platform.status = StatusEnum.FAILURE
                platform.containers?.forEach { it.status = StatusEnum.FAILURE }
            }
            ProblemEnum.BOTH -> {
                val breakdownProblemId = findBreakdownByValue(problem).id
                val failReasonProblemId = findFailReasonByValue(failProblem!!).id
                platform.breakdownReasonId = breakdownProblemId
                platform.failureReasonId = failReasonProblemId
                platform.status = StatusEnum.FAILURE
                platform.containers?.forEach { it.status = StatusEnum.FAILURE }
            }
        }
        platform.problemComment = problemComment
        updateTimer()
        realm.commitTransaction()
    }

    fun updatePlatformStatus(platformId: Int, status: Int) {
        val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
            .findFirst()!!
        realm.beginTransaction()
        updateTimer()
        if (platform.status == StatusEnum.EMPTY) {platform.status = status}
        realm.commitTransaction()
    }

    fun updateContainerStatus(containerId: Int, status: Int) {
        val container = realm.where(ContainerEntity::class.java).equalTo("containerId", containerId)
            .findFirst()!!
        realm.beginTransaction()
        updateTimer()
        if (container.status == StatusEnum.EMPTY) container.status = status
        realm.commitTransaction()
    }

    fun findWayTask(): WayTaskEntity {
        return realm.copyFromRealm(realm.where(WayTaskEntity::class.java).findFirst()!!)
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

    fun findPlatformEntity(platformId: Int): PlatformEntity? {
        return if (realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
                .findFirst() == null
        ) {
            null
        } else {
                realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
                    .findFirst()!!
        }
    }

    /** добавление фото в платформу **/
    fun updateMediaPlatform(photoFor: Int, platformId: Int, photoPath: String) {
        realm.beginTransaction()
        val platformEntity = realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()
        updateTimer()
        when (photoFor) {
            PhotoTypeEnum.forAfterMedia -> {
                platformEntity?.mediaAfter?.add(photoPath)
            }
            PhotoTypeEnum.forBeforeMedia -> {
                platformEntity?.mediaBefore?.add(photoPath)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                platformEntity?.mediaPlatformProblem?.add(photoPath)
            }
            PhotoTypeEnum.forContainerProblem -> {
                platformEntity?.mediaContainerProblem?.add(photoPath)
            }
        }
        realm.commitTransaction()
    }

    /** удалить фото с платформы **/
    fun removeMediaPlatformEntity(photoFor: Int, imagePath: String, platformId: Int) {
        realm.beginTransaction()
        val platformEntity = realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId).findFirst()
        updateTimer()
        when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                platformEntity?.mediaBefore?.remove(imagePath)
            }
            PhotoTypeEnum.forAfterMedia -> {
                platformEntity?.mediaAfter?.remove(imagePath)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                platformEntity?.mediaPlatformProblem?.remove(imagePath)
            }
            PhotoTypeEnum.forContainerProblem -> {
                platformEntity?.mediaContainerProblem?.remove(imagePath)
            }
        }
        realm.commitTransaction()
    }

    fun findPlaFormsSynchronize(): ArrayList<PlatformEntity> {
        val platforms = findWayTask().platfroms

        fun toBase64(list: RealmList<String>) = list.mapTo(RealmList()) { MyUtil.imageToBase64(it) }

        return platforms!!.mapTo(ArrayList()) {
            PlatformEntity(
                platformId = it.platformId, address = it.address, lat = it.lat, lon = it.lon, name = it.name,
                containers = it.containers, srpId = it.srpId, status = it.status,
                mediaAfter = it.mediaAfter, mediaBefore = it.mediaBefore,
                mediaPlatformProblem = it.mediaPlatformProblem,
                mediaContainerProblem = it.mediaContainerProblem, breakdownReasonId = it.breakdownReasonId,
                failureReasonId = it.failureReasonId, problemComment = it.problemComment, updatedAt = it.updatedAt,
                organisationId = it.organisationId, woId = it.woId
            )
        }
    }

    private fun updateTimer() {
        AppPreferences.lastUpdateTime = MyUtil.timeStamp()
    }

}