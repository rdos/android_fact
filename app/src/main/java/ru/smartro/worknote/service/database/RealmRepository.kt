package ru.smartro.worknote.service.database

import android.util.Log
import com.yandex.mapkit.geometry.Point
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
import kotlin.math.round

class RealmRepository(private val realm: Realm) {
    private val TAG : String = "RealmRepository--AAA"
    fun insertWayTask(response: Workorder) {

        fun mapMedia(data: List<String>): RealmList<ImageEntity> {
            return data.mapTo(RealmList()) { ImageEntity(image = it, date = 0, RealmList()) }
        }

        fun mapContainers(list: List<Container>): RealmList<ContainerEntity> {
            return list.mapTo(RealmList()) {
                ContainerEntity(
                    client = it.client, contacts = it.contacts, failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, containerId = it.id,
                    isActiveToday = it.isActiveToday,/* breakdownReasonId = it.breakdownReasonId,*/
                    number = it.number, status = it.status, typeId = it.typeId,
                    constructiveVolume = it.constructiveVolume, typeName = it.typeName, volume = it.volume
                )
            }
        }

        fun mapPlatforms(data: List<Platform>): RealmList<PlatformEntity> {
            return data.mapTo(RealmList()) {
                PlatformEntity(
                    address = it.address, afterMedia = mapMedia(it.afterMedia),
                    beforeMedia = mapMedia(it.beforeMedia), beginnedAt = it.beginnedAt, containers = mapContainers(it.containers),
                    coords = RealmList(it.coords[0], it.coords[1]), failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, /*breakdownReasonId = it.breakdownReasonId,*/
                    finishedAt = it.finishedAt, platformId = it.id,
                    name = it.name, updateAt = 0, srpId = it.srpId, status = it.status, kgoVolume = 0.0, icon = it.icon
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

        realm.executeTransaction { realm ->
            realm.insert(wayTask)
        }
    }

    fun clearData() {
        realm.executeTransaction { realm ->
            realm.deleteAll()
        }
    }

    fun insertBreakDown(entities: List<BreakDownEntity>) {
        realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
    }

    // TODO: 26.10.2021 oopS errorS "${entities.size}"
    //  так надо чтобы возможно поймать плавающую ошибку :(
    fun insertFailReason(entities: List<FailReasonEntity>) {
        Log.d(TAG, "insertFailReason.before ${entities.size}")
        realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
        Log.d(TAG, "insertFailReason.after")
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
    }


    private fun findBreakdownByValue(realm: Realm, problem: String): BreakDownEntity {
        return realm.copyFromRealm(
            realm.where(BreakDownEntity::class.java).equalTo("problem", problem).findFirst()!!
        )
    }

    fun findFailReasonByValue(realm: Realm, problem: String): FailReasonEntity {
        return realm.where(FailReasonEntity::class.java).equalTo("problem", problem).findFirst()!!

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

    fun findCancelWayReasonByValue(reason: String): Int {
        return realm.where(CancelWayReasonEntity::class.java).equalTo("problem", reason)
            .findFirst()?.id!!
    }

    /** добавление заполненности контейнера **/
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?, comment: String?) {
        realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()
            container.volume = volume
            container.comment = comment
            if (container.status == StatusEnum.NEW) {
                container.status = StatusEnum.SUCCESS
            }

            platformEntity?.beginnedAt = MyUtil.currentTime()
            updateTimer(platformEntity)
        }
    }

    fun clearContainerVolume(platformId: Int, containerId: Int) {
        realm.executeTransaction {
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!
            container.volume = null
            container.comment = null
            if (container.status == StatusEnum.SUCCESS)
                container.status = StatusEnum.NEW
            updateTimer(platformEntity)
        }
    }

    fun updateContainerProblem(platformId: Int, containerId: Int, problemComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platform = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!
            when (problemType) {
                ProblemEnum.BREAKDOWN -> {
                    val problemId = findBreakdownByValue(realm, problem).id
                    //  container.breakdownReasonId = problemId
                    container.status = StatusEnum.ERROR
                }
                ProblemEnum.ERROR -> {
                    val problemId = findFailReasonByValue(realm, problem).id
                    container.failureReasonId = problemId
                    container.status = StatusEnum.ERROR
                }
                ProblemEnum.BOTH -> {
                    val breakdownProblemId = findBreakdownByValue(realm, problem).id
                    val failReasonProblemId = findFailReasonByValue(realm, failProblem!!).id
                    //      container.breakdownReasonId = breakdownProblemId
                    container.failureReasonId = failReasonProblemId
                    container.status = StatusEnum.ERROR
                }
            }
            container.failureComment = problemComment
            updateTimer(platform)
        }
    }

    fun updatePlatformProblem(platformId: Int, failureComment: String, problemType: ProblemEnum, problem: String, failProblem: String?) {
        realm.executeTransaction { realm ->
            val platform = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!
            when (problemType) {
                ProblemEnum.ERROR -> {
                    val problemId = findFailReasonByValue(realm, problem).id
                    platform.failureReasonId = problemId
                    var platformStatus = StatusEnum.ERROR
                    platform.containers.forEach {
                        if (it.status != StatusEnum.SUCCESS) it.status = StatusEnum.ERROR
                        else platformStatus = StatusEnum.UNFINISHED
                    }
                    platform.status = platformStatus
                }
                ProblemEnum.BOTH -> {
                    val failReasonProblemId = findFailReasonByValue(realm, failProblem!!).id
                    platform.failureReasonId = failReasonProblemId
                    var platformStatus = StatusEnum.ERROR
                    platform.containers.forEach {
                        if (it.status != StatusEnum.SUCCESS) it.status = StatusEnum.ERROR
                        else platformStatus = StatusEnum.UNFINISHED
                    }
                    platform.status = platformStatus
                }
                ProblemEnum.BREAKDOWN -> {
                    val breakdownReasonId = findBreakdownByValue(realm, problem).id
                    //platform.breakdownReasonId = breakdownReasonId

                }
            }
            platform.failureComment = failureComment
            updateTimer(platform)
        }
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        realm.executeTransaction { realm ->
            val platform = realm.where(PlatformEntity::class.java).equalTo("platformId", platformId)
                .findFirst()!!
            if (platform.status == StatusEnum.NEW) {
                platform.status = status
            }
            updateTimer(platform)
        }
    }

    fun findWayTask(): WayTaskEntity {
        realm.refresh()
        // TODO: 25.10.2021 !!!???
        //  return WayTaskEntity() is fail
        val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()
        if (wayTaskEntity != null) {
            return realm.copyFromRealm(wayTaskEntity)
        }
        return WayTaskEntity()
    }

    fun findLastPlatforms(): List<PlatformEntity> {
        realm.refresh()
        val lastSynchroTime = AppPreferences.lastSynchroTime
        return realm.copyFromRealm(
            realm.where(PlatformEntity::class.java).greaterThan("updateAt", lastSynchroTime)
                .findAll()
        )
    }

    fun findPlatforms30min(): List<PlatformEntity> {
        val minutes = 30 * 60 * 1000
        val lastSynchroTime = AppPreferences.lastSynchroTime
        return realm.where(PlatformEntity::class.java)
            .greaterThan("updateAt", lastSynchroTime)
            .lessThanOrEqualTo("updateAt", lastSynchroTime + minutes)
            .findAll()
    }

    fun updatePlatformNetworkStatus(list: List<PlatformEntity>) {
        realm.executeTransaction {
            list.forEach {
                val platform = findPlatformEntity(it.platformId!!)
                if (platform.status != StatusEnum.NEW && !platform.networkStatus!!) {
                    platform.networkStatus = true
                }
            }
        }
    }

    fun findAllPlatforms(): List<PlatformEntity> {
        return realm.copyFromRealm(
            realm.where(PlatformEntity::class.java)
                .findAll()
        )
    }

    fun findAllContainerInPlatform(platformId: Int): List<ContainerEntity> {
        val platform = realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()!!
        return realm.copyFromRealm(platform.containers)
    }

    fun findContainersVolume(): Double {
        var totalKgoVolume = 0.0
        var totalContainersVolume = 0.0


        fun doublePercent(percent: Double?) =
            when (percent) {
                0.00 -> 0.0
                0.25 -> 25.0
                0.50 -> 50.0
                0.75 -> 75.0
                1.00 -> 100.0
                1.25 -> 125.0
                else -> 0.0
            }

        val allContainers = realm.copyFromRealm(
            realm.where(ContainerEntity::class.java)
                .findAll()
        )

        allContainers.forEach {
            val filledVolume = it.constructiveVolume!! * (doublePercent(it.volume) / 100)
            totalContainersVolume += filledVolume
        }

        val allPlatforms = realm.copyFromRealm(
            realm.where(PlatformEntity::class.java)
                .findAll()
        )

        allPlatforms.forEach {
            totalKgoVolume += it.kgoVolume
        }
        val total = totalContainersVolume + totalKgoVolume
        Log.d(TAG, "total=${total}")
        val totalRound = round(total * 100) / 100
        Log.d(TAG, "totalRound=${totalRound}")
        return totalRound
    }

    fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
        return realm.copyFromRealm(
            realm.where(PlatformEntity::class.java)
                .findAll()
        ).find { it.coords[0] == lat && it.coords[1] == lon }!!
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return realm.copyFromRealm(realm.createObjectFromJson(clazz, json)!!)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()!!
    }

    fun findContainerEntity(containerId: Int) =
        realm.where(ContainerEntity::class.java)
            .equalTo("containerId", containerId)
            .findFirst()!!

    fun findContainerProgress(): List<Int> {
        val result = realm.copyFromRealm(realm.where(ContainerEntity::class.java).findAll())
        val servedContainersCount = result.filter { it.status != StatusEnum.NEW }.size
        val allCount = result.size
        return listOf(servedContainersCount, allCount)
    }

    fun findPlatformProgress(): List<Int> {
        val result = realm.copyFromRealm(realm.where(PlatformEntity::class.java).findAll())
        val servedPlatformsCount = result.filter { it.status != StatusEnum.NEW }.size
        val allCount = result.size
        return listOf(servedPlatformsCount, allCount)
    }

    /** добавление фото в платформу **/
    fun updatePlatformMedia(imageFor: Int, platformId: Int, imageBase64: String, coords: Point) {
        realm.executeTransaction { realm ->
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()
            when (imageFor) {
                PhotoTypeEnum.forAfterMedia -> {
                    platformEntity?.afterMedia?.add(
                        ImageEntity(imageBase64, MyUtil.timeStamp(), RealmList(coords.latitude, coords.longitude))
                    )
                }
                PhotoTypeEnum.forBeforeMedia -> {
                    platformEntity?.beginnedAt = MyUtil.currentTime()
                    platformEntity?.beforeMedia?.add(
                        ImageEntity(imageBase64, MyUtil.timeStamp(), RealmList(coords.latitude, coords.longitude))
                    )
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    platformEntity?.failureMedia?.add(
                        ImageEntity(imageBase64, MyUtil.timeStamp(), RealmList(coords.latitude, coords.longitude))
                    )
                }
                PhotoTypeEnum.forKGO -> {
                    platformEntity?.kgoMedia?.add(
                        ImageEntity(imageBase64, MyUtil.timeStamp(), RealmList(coords.latitude, coords.longitude))
                    )
                }
            }
            updateTimer(platformEntity)
        }
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: Double) {
        realm.executeTransaction { realm: Realm ->
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!

            platformEntity.kgoVolume = kgoVolume
        }
    }

    fun updateContainerMedia(platformId: Int, containerId: Int, imageBase64: String, coords: Point) {
        realm.executeTransaction { realm ->
            val containerEntity = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.failureMedia.add(
                ImageEntity(imageBase64, MyUtil.timeStamp(), RealmList(coords.latitude, coords.longitude))
            )
            updateTimer(platformEntity)
        }
    }

    /** удалить фото с контейнера **/
    fun removeContainerMedia(platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        realm.executeTransaction { realm ->
            val containerEntity = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.failureMedia.remove(imageBase64)
            updateTimer(platformEntity)
        }
    }

    /** удалить фото с платформы **/
    fun removePlatformMedia(imageFor: Int, imageBase64: ImageEntity, platformId: Int) {
        realm.executeTransaction { realm ->
            val platformEntity = realm.where(PlatformEntity::class.java)
                .equalTo("platformId", platformId).findFirst()
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
                PhotoTypeEnum.forKGO -> {
                    platformEntity?.kgoMedia?.remove(imageBase64)
                }
            }
            updateTimer(platformEntity)
        }
    }

    private fun updateTimer(entity: PlatformEntity?) {
        entity?.updateAt = MyUtil.timeStamp()
    }

}
