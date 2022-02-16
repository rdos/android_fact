package ru.smartro.worknote.work

import android.util.Log
import com.yandex.mapkit.geometry.Point
import io.realm.*
import ru.smartro.worknote.Inull
import ru.smartro.worknote.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.NonPickupEnum
import ru.smartro.worknote.util.StatusEnum
import java.lang.Exception
import kotlin.math.round

class RealmRepository(private val p_realm: Realm) {
    private val TAG: String = "RealmRepository"

    fun insertWayTask(woRKoRDeRknow1: WoRKoRDeR_know1) {

        fun mapMedia(data: List<String>): RealmList<ImageEntity> {
            return data.mapTo(RealmList()) { ImageEntity(image = it, date = 0,
                coords = RealmList()) }
        }


        // TODO: 29.10.2021 ! it.volume = 0.0 ??Error
        fun mapContainers(list: List<CoNTaiNeR_know1>, workorderId: Int): RealmList<ContainerEntity> {
            return list.mapTo(RealmList()) {
//                var volumeReal : Double? = null
//                if (it.volume >  0) {
//                    Log.e(TAG ,"mapContainers.it.volume >  0")
//                    volumeReal = it.volume
//                    Log.e(TAG ,"mapContainers.volumeReal = ${volumeReal}")
//                }
                ContainerEntity(
                    workOrderId = workorderId,
                    client = it.client,
                    contacts = it.contacts,
                    failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId,
                    containerId = it.id,
                    isActiveToday = it.isActiveToday,/* breakdownReasonId = it.breakdownReasonId,*/
                    number = it.number,
                    status = it.status,
                    typeId = it.typeId,
                    constructiveVolume = it.constructiveVolume,
                    typeName = it.typeName,
                    volume = it.volume
                )
            }
        }

        fun mapPlatforms(data: List<Platform_know1>, workorderId: Int): RealmList<PlatformEntity> {
             val result = data.mapTo(RealmList()) {
                PlatformEntity(
                    workOrderId = workorderId,
                    address = it.address,
                    afterMedia = mapMedia(it.afterMedia),
                    beforeMedia = mapMedia(it.beforeMedia),
                    beginnedAt = it.beginnedAt,
                    containers = mapContainers(it.coNTaiNeRKnow1s, workorderId),
                    coords = RealmList(it.coords[0], it.coords[1]),
                    failureMedia = mapMedia(it.failureMedia),
                    failureReasonId = it.failureReasonId, /*breakdownReasonId = it.breakdownReasonId,*/
                    finishedAt = it.finishedAt,
                    platformId = it.id,
                    name = it.name,
                    updateAt = 0,
                    srpId = it.srpId,
                    status = it.status,
                    /** volumeKGO = null,*/ icon = it.icon,
                    orderTimeEnd = it.orderEndTime,
                    orderTimeStart = it.orderStartTime,
                    orderTimeAlert = it.orderAlertTime,
                    orderTimeWarning = it.orderWarningTime,
                    kgoServed = KGOEntity().copyKGOEntity(it.kgo_served),
                    kgoRemaining = KGOEntity().copyKGOEntity(it.kgo_remaining)
                )
            }
            return result
        }

        fun mapStart(data: STaRT_know1?): StartEntity? {
            var result: StartEntity? = null
            if (data != null) {
                result= StartEntity(
                    coords = RealmList(data.coords[0], data.coords[1]),
                    name = data.name,
                    id = data.id
                )
            }
            return result
        }


        val wayTask = WorkOrderEntity(
            id = woRKoRDeRknow1.id,
            start_at = MyUtil.currentTime(),
            name = woRKoRDeRknow1.name,
            platforms = mapPlatforms(woRKoRDeRknow1.platformKnow1s, woRKoRDeRknow1.id),
            start = mapStart(woRKoRDeRknow1.STaRTknow1)
        )

        insUpdWorkOrders(wayTask, true)
    }

    /** WORKORDER_ST ***WORKORDER_ART*** WORKORDER_ST ***WORKORDER_ART*** */
    /** WORKORDER_ST ***WORKORDER_ART*** WORKORDER_ST ***WORKORDER_ART*** */
    /** WORKORDER_ST ***WORKORDER_ART*** WORKORDER_ST ***WORKORDER_ART*** */


    /** WORKORDER_ST ***WORKORDER_ART*** WORKORDER_ST ***WORKORDER_ART*** */


    fun findWorkOrders(workOrderId: Int? = null): List<WorkOrderEntity> {
        var res = emptyList<WorkOrderEntity>()
        p_realm.executeTransaction { realm ->
            val workOrderS: RealmResults<WorkOrderEntity>
            if (workOrderId == null) {
                workOrderS = getWorkOrderQuery().findAll()
            } else {
                if (workOrderId == Inull) {
                    workOrderS = p_realm.where(WorkOrderEntity::class.java).findAll()
                } else {
                    workOrderS = p_realm.where(WorkOrderEntity::class.java).equalTo("id", workOrderId).findAll()
                }
            }

            if(workOrderS.isNotEmpty()){
                res = realm.copyFromRealm(workOrderS)
            }

        }
        return res
    }


    /** WORKORDER_END ***WORKORDER_END***WORKORDER_END*****WORKORDER_END***WORKORDER_END******** */
    /** WORKORDER_END ***WORKORDER_END***WORKORDER_END*****WORKORDER_END***WORKORDER_END*******/
    //нет знаний =_know от слова -know ledge
    //0 -не уверен что нужно = ??!
    private fun refreshRealm_know0(){
        try {
            p_realm.refresh()
        } catch (ex:Exception){
            // TODO:
        }
    }

    fun clearBase() {
        p_realm.executeTransaction { realm ->
            realm.deleteAll()
        }
    }

    fun insertBreakDown(entities: List<BreakDownEntity>) {
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
    }

    // TODO: 26.10.2021 oopS errorS "${entities.size}"
    //  так надо чтобы возможно поймать плавающую ошибку :(
    fun insertFailReason(entities: List<FailReasonEntity>) {
        Log.d(TAG, "insertFailReason.before ${entities.size}")
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
        Log.d(TAG, "insertFailReason.after")
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        Log.d(TAG, "insertCancelWayReason.before  ${entities.size}")
        p_realm.executeTransaction { realm ->
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
        val found = p_realm.copyFromRealm(p_realm.where(FailReasonEntity::class.java).findAll())
        return found.map { it.problem!! }
    }

    fun findAllBreakDown(): List<String> {
        val found = p_realm.copyFromRealm(p_realm.where(BreakDownEntity::class.java).findAll())
        return found.map { it.problem!! }
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return p_realm.copyFromRealm(p_realm.where(CancelWayReasonEntity::class.java).findAll())
    }

    fun findCancelWayReasonByValue(reason: String): Int {
        return p_realm.where(CancelWayReasonEntity::class.java).equalTo("problem", reason)
            .findFirst()?.id!!
    }

    fun updateSelectionVolume(platformId: Int, volumePickup: Double?) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.volumePickup = volumePickup
            if (volumePickup == null) {
                platformEntity?.pickupMedia = RealmList()
            }
            platformEntity?.beginnedAt = MyUtil.currentTime()
            setEntityUpdateAt(platformEntity)
        }
    }

    /** добавление заполненности контейнера **/
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?, comment: String?) {
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            container.volume = volume
            container.comment = comment
            if (container.status == StatusEnum.NEW) {
                container.status = StatusEnum.SUCCESS
            }

            platformEntity?.beginnedAt = MyUtil.currentTime()
            setEntityUpdateAt(platformEntity)
        }
    }

    fun updateContainersVolumeIfnNull(platformId: Int, volume: Double) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.containers?.forEach {
                if (it.volume == null) {
                    if (it.isActiveToday) {
                        it.volume = volume
                        if (it.status == StatusEnum.NEW) {
                            it.status = StatusEnum.SUCCESS
                        }
                    }

                }
            }

            platformEntity?.beginnedAt = MyUtil.currentTime()
            setEntityUpdateAt(platformEntity)
        }
    }


    fun clearContainerVolume(platformId: Int, containerId: Int) {
        p_realm.executeTransaction {
            val container = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            container.volume = null
            // TODO: 02.11.2021 !!!
            if (container.failureReasonId == null) {
                container.comment = null
            }
            if (container.status == StatusEnum.SUCCESS)
                container.status = StatusEnum.NEW
            setEntityUpdateAt(platformEntity)
        }
    }

    fun updateNonPickupContainer(platformId: Int, containerId: Int,
                                 problemComment: String, nonPickupType: NonPickupEnum,
                                 problem: String) {
        Log.d(TAG, "updateContainerProblem.before platformId=${platformId}")
        Log.d(TAG, "updateContainerProblem.containerId=${containerId}, problemComment=${problemComment}")
        Log.d(TAG, "updateContainerProblem.problemType=${nonPickupType}, problem=${problem}")
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            when (nonPickupType) {
                NonPickupEnum.BREAKDOWN -> {
                    val problemId = findBreakdownByValue(realm, problem).id
                    container.breakdownReasonId = problemId
                    container.status = StatusEnum.ERROR
                }
                NonPickupEnum.FAILURE -> {
                    val problemId = findFailReasonByValue(realm, problem).id
                    container.failureReasonId = problemId
                    container.status = StatusEnum.ERROR

                }
            }
            container.comment = problemComment
            setEntityUpdateAt(platform)
        }
    }

    fun updateNonPickupPlatform(platformId: Int, failureComment: String, problem: String) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            val problemId = findFailReasonByValue(realm, problem).id
            platform.failureReasonId = problemId
            var platformStatus = StatusEnum.ERROR
            platform.containers.forEach {
                if (it.status != StatusEnum.SUCCESS) it.status = StatusEnum.ERROR
                else platformStatus = StatusEnum.UNFINISHED
            }
            platform.status = platformStatus

            platform.failureComment = failureComment
            val workOrder = getWorkOrderQuery().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()
            setEntityUpdateAt(platform)
        }
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform().equalTo("platformId", platformId)
                .findFirst()!!
            if (platform.status == StatusEnum.NEW) {
                platform.status = status
            }
            val workOrder = getWorkOrderQuery().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()

            setEntityUpdateAt(platform)
        }
    }

    fun findWayTasks(): List<WorkOrderEntity> {
        val res = p_realm.where(WorkOrderEntity::class.java).findAll()
        if (res != null) {
            return p_realm.copyFromRealm(res)
        }
        return emptyList()
    }

    fun findPlatforms(): List<PlatformEntity> {
        p_realm.refresh()
        // TODO: 25.10.2021 !!!???
        //  return WayTaskEntity() is fail

        val res = getQueryPlatform().sort("updateAt").findAll()
        if (res != null) {
            return p_realm.copyFromRealm(res)
        }
        return emptyList()
    }



    // TODO:
    fun findLastPlatforms(): List<PlatformEntity> {
//        p_realm.refresh()
        val lastSynchroTime = AppPreferences.lastSynchroTime
        return p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).greaterThan("updateAt", lastSynchroTime)
                .findAll()
        )
    }

    fun findPlatforms30min(): List<PlatformEntity> {
        val minutes = 30 * 60 * 1000
        val lastSynchroTime = AppPreferences.lastSynchroTime
        return p_realm.where(PlatformEntity::class.java)
            .greaterThan("updateAt", lastSynchroTime)
            .lessThanOrEqualTo("updateAt", lastSynchroTime + minutes)
            .findAll()
    }



    fun updatePlatformNetworkStatus(list: List<PlatformEntity>) {
        p_realm.executeTransaction {
            list.forEach {
                val platform = findPlatformEntity(it.platformId!!)
                if (platform.status != StatusEnum.NEW && !platform.networkStatus!!) {
                    platform.networkStatus = true
                }
            }
        }
    }

    fun findAllPlatforms(): List<PlatformEntity> {
        return p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java)
                .findAll()
        )
    }

    fun findAllContainerInPlatform(platformId: Int): List<ContainerEntity> {
        val platform = p_realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()!!
        return p_realm.copyFromRealm(platform.containers)
    }

    fun findContainersVolume(workOrderId: Int): Double {
        var totalKgoVolume = 0.0
        var totalContainersVolume = 0.0
        Log.d(TAG, "findContainersVolume.before")


        val allContainers = p_realm.copyFromRealm(
            getQueryContainer()
                .equalTo("workOrderId", workOrderId)
                .findAll()
        )
        Log.d(TAG, "findContainersVolume.totalContainersVolume=${totalContainersVolume}")
        allContainers.forEach { container ->
            container.volume?.let{
                val filledVolume = container.constructiveVolume!! * (container.convertVolumeToPercent() / 100)
                Log.d(TAG, "findContainersVolume.filledVolume=${filledVolume}")
                totalContainersVolume += filledVolume
                Log.d(TAG, "findContainersVolume.totalContainersVolume=${totalContainersVolume}")
            }
        }
        Log.d(TAG, "findContainersVolume.totalContainersVolume=${totalContainersVolume}")

        val allPlatforms = p_realm.copyFromRealm(
            getQueryPlatform()
                .equalTo("workOrderId", workOrderId)
                .findAll()
        )

        allPlatforms.forEach { pl ->
            if (pl.isServedKGONotEmpty()) {
                pl.kgoServed!!.volume?.let {
                    totalKgoVolume += it
                }
            }
            pl.volumePickup?.let {
                totalKgoVolume += it
            } //код всегда показывает, где(когда) Люди ошиблись
        }
        Log.d(TAG, "findContainersVolume.totalKgoVolume=${totalKgoVolume}")

        val result = totalContainersVolume + totalKgoVolume
        Log.d(TAG, "findContainersVolume.result=${result}")
        val resultRound = round(result * 100) / 100
        Log.d(TAG, "findContainersVolume.resultRound=${resultRound}")
        return resultRound
    }

    fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
        return p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java)
                .findAll()
        ).find { it.coords[0] == lat && it.coords[1] == lon }!!
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return p_realm.copyFromRealm(p_realm.createObjectFromJson(clazz, json)!!)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        return getQueryPlatform()
            .equalTo("platformId", platformId)
            .findFirst()!!
    }

    fun findContainerEntity(containerId: Int) =
        getQueryContainer()
            .equalTo("containerId", containerId)
            .findFirst()!!

//    fun findCountContainerIsServed(): List<Int> {
//        val result = p_realm.copyFromRealm(p_realm.where(ContainerEntity::class.java).findAll())
//        val servedContainersCount = result.filter { it.status != StatusEnum.NEW }.size
//        val allCount = result.size
//        return listOf(servedContainersCount, allCount)
//    }

//    fun findCountPlatformIsServed(): List<Int> {
//        val result = p_realm.copyFromRealm(p_realm.where(PlatformEntity::class.java).findAll())
//        val servedPlatformsCount = result.filter { it.status != StatusEnum.NEW }.size
//        val allCount = result.size
//        return listOf(servedPlatformsCount, allCount)
//    }

    fun findPlatformsIsServed(): List<PlatformEntity> {
        val result = p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).findAll().sort("updateAt")
        )
        val filteredList = result.filter { it.status != StatusEnum.NEW }
        return filteredList
    }


    /** добавление фото в платформу **/
    fun updatePlatformMedia(
        imageFor: Int, platformId: Int, imageBase64: String,
        coords: Point, currentCoordinateAccuracy: String, lastKnownLocationTime: Long
    ) {
        p_realm.executeTransaction { realm ->
            val imageEntity = ImageEntity(imageBase64, MyUtil.timeStamp(),
                RealmList(coords.latitude, coords.longitude), currentCoordinateAccuracy, lastKnownLocationTime)
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            when (imageFor) {
                PhotoTypeEnum.forAfterMedia -> {
                    platformEntity?.afterMedia?.add(imageEntity)
                }
                PhotoTypeEnum.forBeforeMedia -> {
                    platformEntity?.beginnedAt = MyUtil.currentTime()
                    platformEntity?.beforeMedia?.add(imageEntity)
                }
                PhotoTypeEnum.forPlatformProblem -> {
                    platformEntity?.failureMedia?.add(imageEntity)
                }
                PhotoTypeEnum.forPlatformPickupVolume -> {
                    platformEntity?.pickupMedia?.add(imageEntity)
                }
                PhotoTypeEnum.forServedKGO -> {
                    platformEntity!!.addServerKGOMedia(imageEntity)
                }
                PhotoTypeEnum.forRemainingKGO -> {
                    platformEntity!!.addRemainingKGOMedia(imageEntity)
                }
            }
            setEntityUpdateAt(platformEntity)
        }
    }

    // TODO: 29.10.2021 !!!???
    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        p_realm.executeTransaction { realm: Realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            if (isServedKGO) {
                platformEntity.setServedKGOVolume(kgoVolume)
            } else {
                platformEntity.setRemainingKGOVolume(kgoVolume)
            }
        }
    }

    fun updateContainerMedia(
        platformId: Int, containerId: Int, imageBase64: String,
        coords: Point, currentCoordinateAccuracy: String, lastKnownLocationTime: Long
    ) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.failureMedia.add(
                ImageEntity(imageBase64, MyUtil.timeStamp(),
                    RealmList(coords.latitude, coords.longitude),
                    currentCoordinateAccuracy, lastKnownLocationTime)
            )
            setEntityUpdateAt(platformEntity)
        }
    }
    /** удалить фото с контейнера **/
    fun removeContainerMedia(platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        p_realm.executeTransaction { realm ->
            val containerEntity = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.failureMedia.remove(imageBase64)
            setEntityUpdateAt(platformEntity)
        }
    }

    /** удалить фото с платформы **/
    fun removePlatformMedia(imageFor: Int, imageBase64: ImageEntity, platformId: Int) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
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

                PhotoTypeEnum.forPlatformPickupVolume -> {
                    platformEntity!!.pickupMedia.remove(imageBase64)
                }
                PhotoTypeEnum.forServedKGO -> {
                    platformEntity!!.kgoServed!!.media.remove(imageBase64)
                }
                PhotoTypeEnum.forRemainingKGO -> {
                    platformEntity!!.kgoRemaining!!.media.remove(imageBase64)
                }
            }
            setEntityUpdateAt(platformEntity)
        }
    }

    private fun setEntityUpdateAt(entity: PlatformEntity?) {
        entity?.updateAt = MyUtil.timeStamp()
    }


    private fun insUpdWorkOrders(workOrder: WorkOrderEntity, isInfoChange: Boolean = true): WorkOrderEntity  {
        p_realm.executeTransaction { realm ->
            if (isInfoChange) {
                workOrder.calcInfoStatistics()
            }
            realm.insertOrUpdate(workOrder)
        }
        return workOrder
    }


    fun setNextProcessDate(workOrder: WorkOrderEntity) {
        //психи крики всем РОСТ
    }

    fun setProgressData(oldWorkOrder: WorkOrderEntity): WorkOrderEntity {
        var result: WorkOrderEntity = oldWorkOrder
        p_realm.executeTransaction { realm ->
            val workOrder = p_realm.where(WorkOrderEntity::class.java).
            equalTo("id", oldWorkOrder.id).findFirst()!!
            workOrder.progress_at = MyUtil.currentTime()
            for (platform in workOrder.platforms) {
                platform.isWorkOrderProgress = true
                for (container in platform.containers) {
                    container.isWorkOrderProgress = true
                }
            }
            result = p_realm.copyFromRealm(workOrder)
        }
        return result
    }

    fun setCompleteData(oldWorkOrder: WorkOrderEntity): WorkOrderEntity  {
        var result: WorkOrderEntity = oldWorkOrder
        p_realm.executeTransaction { realm ->
            val workOrder = p_realm.where(WorkOrderEntity::class.java).
            equalTo("id", oldWorkOrder.id).findFirst()!!
            workOrder.end_at = MyUtil.currentTime()
            workOrder.progress_at = null
            for (platform in workOrder.platforms) {
                platform.isWorkOrderComplete = true
                for (container in platform.containers) {
                    container.isWorkOrderComplete = true
                }
            }
            result = p_realm.copyFromRealm(workOrder)
        }
        return result

    }

    public fun hasNotWorkOrderInProgress(): Boolean {
       return !hasWorkOrderInProgress_know0()
    }

    /** всё что есть объёкт. У объекта есть
    а) свойства
    и[hasWorkOrderInProgress_know0.true - это fun или свойства?]нформация
    b) fun
    **/
    fun hasWorkOrderInProgress_know0(): Boolean {
        var res = true
        // TODO:rdos из бд лучше же
        p_realm.executeTransaction { realm ->
            val workOrders = realm.where(WorkOrderEntity::class.java).isNotNull("progress_at").findAll()
            if (workOrders.isEmpty()) {
                res = false
            }
        }
        Log.d(TAG, "hasWorkOrderInProgress_know0.${res}")
        return res
    }

    private fun getQueryPlatform(): RealmQuery<PlatformEntity> {
        return p_realm.where(PlatformEntity::class.java)
            .equalTo("isWorkOrderProgress", true)
            .equalTo("isWorkOrderComplete", false)
    }
    private fun getQueryContainer(): RealmQuery<ContainerEntity> {
        return p_realm.where(ContainerEntity::class.java)
            .equalTo("isWorkOrderProgress", true)
            .equalTo("isWorkOrderComplete", false)
    }

    private fun getWorkOrderQuery(): RealmQuery<WorkOrderEntity> {
        return p_realm.where(WorkOrderEntity::class.java).isNotNull("progress_at")
    }
}
