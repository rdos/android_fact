package ru.smartro.worknote.work

import android.util.Log
import io.realm.*
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.andPOintD.LiveRealmData
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.NonPickupEnum
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
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
                    coordLat = it.coords[0],
                    coordLong = it.coords[1],
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
            waste_type_id = woRKoRDeRknow1.waste_type?.id,
            waste_type_name = woRKoRDeRknow1.waste_type?.name,
            waste_type_color = woRKoRDeRknow1.waste_type?.color?.hex,
            platforms = mapPlatforms(woRKoRDeRknow1.platformKnow1s, woRKoRDeRknow1.id),
            start = mapStart(woRKoRDeRknow1.STaRTknow1)
        )

        insUpdWorkOrders(wayTask, true)
    }

    fun <T:RealmObject> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)

    fun findPlatformsLive(): LiveRealmData<PlatformEntity> {
        return LiveRealmData(getQueryPlatform().sort("updateAt").findAllAsync())
    }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //    fun findPlatforms(): List<PlatformEntity> {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        Log.w(TAG, "r_dos/findPlatforms.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        p_realm.refresh()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        var res = emptyList<PlatformEntity>()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        // TODO: 25.10.2021 !!!???
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        //  return WayTaskEntity() is fail
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        Log.w(TAG, "r_dos/findAll.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        val realmResults: RealmResults<PlatformEntity>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        if (workOrderSIntArray.isEmpty()) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            realmResults = getQueryPlatform().findAll()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        } else {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            realmResults = getQueryPlatform().`in`("workOrderId", workOrderSIntArray).sort("updateAt").findAll()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        Log.w(TAG, "r_dos/findAll.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        if (realmResults != null) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            Log.w(TAG, "r_dos/copyFromRealm.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            res = p_realm.copyFromRealm(realmResults)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            Log.w(TAG, "r_dos/copyFromRealm.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            Log.w(TAG, "r_dos/setEmptyImageEntity.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            setEmptyImageEntity(res)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            Log.w(TAG, "r_dos/setEmptyImageEntity.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        Log.w(TAG, "r_dos/findPlatforms.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        return res
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //    }


    /** WORKORDER_ST ***WORKORDER_ART*** WORKORDER_ST ***WORKORDER_ART*** */
    fun findWorkOrdersLive(workOrderId: Int? = null): LiveRealmData<WorkOrderEntity> {
        val workOrderS: RealmResults<WorkOrderEntity>
        if (workOrderId == null) {
            workOrderS = getQueryWorkOrder().findAllAsync()
        } else {
            if (workOrderId == Inull) {
                workOrderS = p_realm.where(WorkOrderEntity::class.java).findAllAsync()
            } else {
                workOrderS = p_realm.where(WorkOrderEntity::class.java).equalTo("id", workOrderId).findAllAsync()
            }
        }

        return LiveRealmData(workOrderS)
    }

    //todo: кака код
    fun findWorkOrders(isFilterMode: Boolean): List<WorkOrderEntity> {
        var res = emptyList<WorkOrderEntity>()
        p_realm.executeTransaction { realm ->
            val workOrderS: RealmResults<WorkOrderEntity>
            if (isFilterMode) {
                workOrderS = getQueryWorkOrder().equalTo("isShowForUser", true).findAll()
            } else {
                workOrderS = getQueryWorkOrder().findAll()
            }

            if(workOrderS.isNotEmpty()){
                res = realm.copyFromRealm(workOrderS)
                res.forEach { workOrderEntity ->
                    setEmptyImageEntity(workOrderEntity.platforms)
                }
            }
        }
        return res
    }

    fun findWorkOrders_Old(workOrderId: Int? = null): List<WorkOrderEntity> {
        var res = emptyList<WorkOrderEntity>()
        p_realm.executeTransaction { realm ->
            val workOrderS: RealmResults<WorkOrderEntity>
            // TODO: 03/06/2022 12:12
            if (workOrderId == null || workOrderId == Inull) {
                workOrderS = getQueryWorkOrder(true).findAll()
            } else {
                workOrderS =getQueryWorkOrder(true).equalTo("id", workOrderId).findAll()
            }

            if(workOrderS.isNotEmpty()){
                res = realm.copyFromRealm(workOrderS)
                res.forEach { workOrderEntity ->
                    setEmptyImageEntity(workOrderEntity.platforms)
                }
            }

        }
        return res
    }


    fun getWorkOrderEntityS(isShowForUser: Boolean): MutableList<WorkOrderEntity> {
        val realmResult = getQueryWorkOrder().equalTo("isShowForUser", isShowForUser).findAll()
        val result = p_realm.copyFromRealm(realmResult)
        return result
    }

    fun getWorkOrderEntity(workorderId: Int): WorkOrderEntity {
        var res = getQueryWorkOrder().equalTo("id", workorderId).findFirst()
        if (res == null) {
            //todo:!r_dos oops!!
            res = WorkOrderEntity()
        }
        return res
    }

    /** FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  */
//
//    fun updateFailureCommentTTEST(platformId: Int, failureComment: String) {
//        p_realm.executeTransaction { realm ->
//            val platform = getQueryPlatform()
//                .equalTo("platformId", platformId)
//                .findFirst()!!
//            platform.failureComment = failureComment
//        }
//    }
//
//    fun addFailureCommentTTEST(platformId: Int, failureComment: String) {
//        p_realm.executeTransaction { realm ->
//            val platform = getQueryPlatform()
//                .equalTo("platformId", platformId)
//                .findFirst()!!
//            platform.failureComment = platform.failureComment + ";" + failureComment
//        }
//    }
    /** FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  *FOR Ttesting  */
    /** WORKORDER_END ***WORKORDER_END***WORKORDER_END*****WORKORDER_END***WORKORDER_END******** */
    //нет знаний =_know от слова -know ledge
    //0 -не уверен что нужно = ??!
    private fun refreshRealm_know0(){
        try {
            p_realm.refresh()
        } catch (ex:Exception){
            // TODO:
        }
    }

    fun clearDataBase() {
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

    private fun findFailReasonByValue(realm: Realm, problem: String): FailReasonEntity {
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

    fun findCancelWayReasonEntity(): List<CancelWayReasonEntity> {
        return p_realm.copyFromRealm(p_realm.where(CancelWayReasonEntity::class.java).findAll())
    }

    fun findCancelWayReasonIdByValue(reason: String): Int {
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
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            container.volume = volume
            if (container.status == StatusEnum.NEW) {
                container.status = StatusEnum.SUCCESS
            }

            platformEntity?.beginnedAt = MyUtil.currentTime()
            setEntityUpdateAt(platformEntity)
        }
    }

    fun updateContainerComment(platformId: Int, containerId: Int, comment: String?) {
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            container.comment = comment
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

    fun updateContainerFailure(platformId: Int, containerId: Int,
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
            platform.containers.forEach {
                if (it.status != StatusEnum.SUCCESS) {
                    it.status = StatusEnum.ERROR
                }
            }

            val isAllSuccess = platform.containers.all { el -> el.status == StatusEnum.SUCCESS  }
            val isAllError = platform.containers.all { el -> el.status == StatusEnum.ERROR  }

            val platformStatus =
                if(isAllSuccess) StatusEnum.SUCCESS
                else if(isAllError) StatusEnum.ERROR
                else StatusEnum.UNFINISHED
            platform.status = platformStatus

            platform.failureComment = failureComment
            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()
            setEntityUpdateAt(platform)
        }
    }

    fun updatePlatformStatus(platformId: Int, status: String) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform().equalTo("platformId", platformId)
                .findFirst()!!

            val containersToCheck = platform.containers.filter { el -> el.isActiveToday }

            val isAllSuccess = containersToCheck.all { el -> el.status == StatusEnum.SUCCESS  }
            val isAllError = containersToCheck.all { el -> el.status == StatusEnum.ERROR  }

            val platformStatus =
                if(isAllSuccess) StatusEnum.SUCCESS
                else if(isAllError) StatusEnum.ERROR
                else StatusEnum.UNFINISHED
            platform.status = platformStatus

            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()

            workOrder?.calcInfoStatistics()

            setEntityUpdateAt(platform)
        }
    }



    fun setEmptyImageEntity(platforms: List<PlatformEntity>) {
        val emptyImageEntityList = RealmList<ImageEntity>()
        platforms.forEach { platform ->
            platform.afterMedia = emptyImageEntityList
            platform.beforeMedia = emptyImageEntityList
            platform.failureMedia = emptyImageEntityList
            platform.pickupMedia = emptyImageEntityList
            platform.kgoRemaining?.media = emptyImageEntityList
            platform.kgoServed?.media = emptyImageEntityList
            platform.containers.forEach { container ->
                container.failureMedia = emptyImageEntityList
                container.breakdownMedia = emptyImageEntityList
            }
        }
    }

    // TODO: copy-past см. PoinT.kt
    fun findPlatformByCoord(coordLat: Double, coordLong: Double, accuracy: Float): PlatformEntity? {
        var res: PlatformEntity? = null
        if (accuracy > 50) {
            return null
        }
        //lat=0,000133755 это 15 метров LAT15M
        val LAT15M = 0.000008917
        val LONG15M = 0.00001488
//        long=0,0002232 это 15 метров
        var koef = 15f
        if (accuracy >= koef) {
            koef = accuracy
        }
        val minLat = coordLat - LAT15M*koef
        val maxLat = coordLat + LAT15M*koef
        val minLong = coordLong - LONG15M*koef
        val maxLong = coordLong + LONG15M*koef
        val platformByCoord = getQueryPlatform()
            .equalTo("status", "new")
            .greaterThanOrEqualTo("coordLat", minLat)
            .lessThanOrEqualTo("coordLat", maxLat)
            .greaterThanOrEqualTo("coordLong", minLong)
            .lessThanOrEqualTo("coordLong", maxLong)
            .findAll()
        if (platformByCoord.isNullOrEmpty()) {
            return res
        }
        if (platformByCoord.size == 1) {
            res = platformByCoord[0]?.let { p_realm.copyFromRealm(it) }
            if (res != null) {
                Log.w(TAG, "res.address=${res.address} ")
            }
        }
        if (res == null) {
            Log.w(TAG, "platformByCoord.count=${platformByCoord.size} ")
        }
        return res
    }

//    fun findPlatformByCoord(point: Point): LiveRealmData<PlatformEntity> {
//        //lat=0,000133755 это 15 метров
//        val LAT15M = 0.000133755
//        val LONG15M = 0.0002232
////        long=0,0002232 это 15 метров
//        val minLat = point.latitude - LAT15M
//        val maxLat = point.latitude + LAT15M
//        val minLong = point.longitude - LONG15M
//        val maxLong = point.longitude + LONG15M
//        val res = getQueryPlatform()
//            .greaterThanOrEqualTo("coordLat", minLat)
//            .lessThanOrEqualTo("coordLat", maxLat)
//            .greaterThanOrEqualTo("coordLong", minLong)
//            .lessThanOrEqualTo("coordLong", maxLong)
//            .findAllAsync()
//        return res.asLiveData()
//    }




    // TODO:
    fun findLastPlatforms(): List<PlatformEntity> {
        refreshRealm_know0()
        //todo: -1 секунда
        val lastSynchroTime = App.getAppParaMS().lastSynchroTimeInSec - 1L
        return p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).greaterThan("updateAt", lastSynchroTime)
                .findAll()
        )
    }

    fun findPlatforms30min(): List<PlatformEntity> {
        refreshRealm_know0()
        val TIME_30MIN_MS = 30 * 60 * 1000
        //todo: -1 секунда
        val lastSynchroTime = App.getAppParaMS().lastSynchroTimeInSec - 1L
        return p_realm.copyFromRealm(p_realm.where(PlatformEntity::class.java)
            .greaterThan("updateAt", lastSynchroTime)
            .lessThanOrEqualTo("updateAt", lastSynchroTime + TIME_30MIN_MS)
            .findAll())
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

    fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity? {
        return p_realm.copyFromRealm(
            getQueryPlatform().findAll()
        ).find { it.coords[0] == lat && it.coords[1] == lon }
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return p_realm.copyFromRealm(p_realm.createObjectFromJson(clazz, json)!!)
    }

    fun findPlatformEntity(platformId: Int): PlatformEntity {
        Log.d("PLATFORM ID :::", "$platformId")
        if(platformId == Inull) {
            return PlatformEntity(name="findPlatformEntity.platformId==Inull")
        }
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

    fun findPlatformsIsNew(): List<PlatformEntity> {
        val result = p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).findAll().sort("updateAt")
        )
        val filteredList = result.filter { it.status == StatusEnum.NEW }
        return filteredList
    }


    /** добавление фото в платформу **/
    fun updatePlatformMedia(imageFor: Int, platformId: Int, imageEntity: ImageEntity) {
        p_realm.executeTransaction { realm ->
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

    fun updateContainerMedia(imageFor: Int,
        platformId: Int, containerId: Int, imageEntity: ImageEntity
    ) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            when (imageFor) {
                PhotoTypeEnum.forContainerFailure -> {
                    containerEntity.failureMedia.add(imageEntity)
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    containerEntity.breakdownMedia.add(imageEntity)
                }
            }

            setEntityUpdateAt(platformEntity)
        }
    }
    /** удалить фото с контейнера **/
    fun removeContainerMedia(photoFor: Int,platformId: Int, containerId: Int, imageBase64: ImageEntity) {
        p_realm.executeTransaction { realm ->
            val containerEntity = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!

            when (photoFor) {
                PhotoTypeEnum.forContainerFailure -> {
                    containerEntity.failureMedia.remove(imageBase64)
                }
                PhotoTypeEnum.forContainerBreakdown -> {
                    containerEntity.breakdownMedia.remove(imageBase64)
                }
            }
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
        entity?.updateAt = MyUtil.timeStampInSec()
    }


    fun insUpdWorkOrders(workOrder: WorkOrderEntity, isInfoChange: Boolean = true): WorkOrderEntity  {
        p_realm.executeTransaction { realm ->
            if (isInfoChange) {
                workOrder.calcInfoStatistics()
            }
            realm.insertOrUpdate(workOrder)
        }
        return workOrder
    }



                private fun todo_know1(workOrderId: Int, isShowForUser: Boolean): WorkOrderEntity {
                    var result: WorkOrderEntity = WorkOrderEntity()
            //        p_realm.executeTransaction { realm ->
                        val workOrderS: RealmResults<WorkOrderEntity>
                        workOrderS = getQueryWorkOrder().equalTo("id", workOrderId).findAll()
                        workOrderS.forEach {
                            it.isShowForUser = isShowForUser
                        }
            //            result = realm.copyFromRealm(workOrderS[0])!!
            //        }
                    return result
                }

    fun setWorkOrderIsShowForUser(workOrderS: List<WorkOrderEntity>) {
        Log.i(TAG, "setWorkOrderIsShowForUser.before")
        p_realm.executeTransaction { realm ->
            for (workorder in workOrderS) {
                todo_know1(workorder.id, workorder.isShowForUser)
            }
        }
        Log.w(TAG, "setWorkOrderIsShowForUser.after")
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

    fun setCompleteWorkOrderData(oldWorkOrder: WorkOrderEntity): WorkOrderEntity  {
        var result: WorkOrderEntity = oldWorkOrder
        p_realm.executeTransaction { realm ->
            val workOrder = getQueryWorkOrder().equalTo("id", oldWorkOrder.id).findFirst()!!
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

    fun hasWorkOrderInNotProgress(): Boolean {
       return !hasWorkOrderInProgress()
    }

    fun hasWorkOrderInProgress(): Boolean {
        var res = true
        p_realm.executeTransaction { realm ->
            val workOrder = getQueryWorkOrder().findFirst()
            if (workOrder == null) {
                res = false
            }
        }
        Log.d(TAG, "hasWorkOrderInProgress.${res}")
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

    private fun getQueryWorkOrder(isForceMode: Boolean = false): RealmQuery<WorkOrderEntity> {
        if (isForceMode) {
            return p_realm.where(WorkOrderEntity::class.java)
        } else {
            return p_realm.where(WorkOrderEntity::class.java).isNotNull("progress_at")
        }
    }
}
