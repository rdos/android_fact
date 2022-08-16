package ru.smartro.worknote.work

import io.realm.*
import ru.smartro.worknote.*
import ru.smartro.worknote.andPOintD.LiveRealmData
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.BreakDownEntity
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.FailReasonEntity
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import kotlin.math.round


class RealmRepository(private val p_realm: Realm) {
    private val TAG: String = "RealmRepository"
    

    // TODO: ][3
    private val mEmptyImageEntityList = RealmList<ImageEntity>()

    fun insertWorkorder(woRKoRDeRknow1List: List<WoRKoRDeR_know1>) {
        val workOrderS = WorkOrderEntity.map(woRKoRDeRknow1List)
        insUpdWorkOrders(workOrderS, true)
    }

    fun <T:RealmObject> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)

    fun findPlatformsLive(): LiveRealmData<PlatformEntity> {
        return LiveRealmData(getQueryPlatform().sort("updateAt").findAllAsync())
    }

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //    fun findPlatforms(): List<PlatformEntity> {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        LoG.warn( "r_dos/findPlatforms.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        p_realm.refresh()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        var res = emptyList<PlatformEntity>()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        // TODO: 25.10.2021 !!!???
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        //  return WayTaskEntity() is fail
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        LoG.warn( "r_dos/findAll.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        val realmResults: RealmResults<PlatformEntity>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        if (workOrderSIntArray.isEmpty()) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            realmResults = getQueryPlatform().findAll()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        } else {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            realmResults = getQueryPlatform().`in`("workOrderId", workOrderSIntArray).sort("updateAt").findAll()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        LoG.warn( "r_dos/findAll.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        if (realmResults != null) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            LoG.warn( "r_dos/copyFromRealm.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            res = p_realm.copyFromRealm(realmResults)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            LoG.warn( "r_dos/copyFromRealm.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            LoG.warn( "r_dos/setEmptyImageEntity.before")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            setEmptyImageEntity(res)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //            LoG.warn( "r_dos/setEmptyImageEntity.after")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //        LoG.warn( "r_dos/findPlatforms.after")
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
    fun findWorkOrders(isFilterMode: Boolean=false): List<WorkOrderEntity> {
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
        log("insertFailReason.before ${entities.size}")
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
        log("insertFailReason.after")
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        log("insertCancelWayReason.before  ${entities.size}")
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

    fun updateVolumePickup(platformId: Int, volumePickup: Double?) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.volumePickup = volumePickup
            if (volumePickup == null) {
                platformEntity?.pickupMedia = RealmList()
            }
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
//   TODO::: Vlad: Это нужно вообще?
//    fun clearContainerVolume(platformId: Int, containerId: Int) {
//        p_realm.executeTransaction {
//            val container = getQueryContainer()
//                .equalTo("containerId", containerId)
//                .findFirst()!!
//            val platformEntity = getQueryPlatform()
//                .equalTo("platformId", platformId)
//                .findFirst()!!
//            container.volume = null
//            // TODO: 02.11.2021 !!!
//            if (container.failureReasonId == null) {
//                container.comment = null
//            }
//            if (container.status == StatusEnum.SUCCESS)
//                container.status = StatusEnum.NEW
//            setEntityUpdateAt(platformEntity)
//        }
//    }

    fun setStateFailureForPlatform(platformId: Int, problem: String, failureComment: String?=null) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!

            val problemId = findFailReasonByValue(realm, problem).id
            platform.failureReasonId = problemId

            platform.containers.forEach { container ->
                if (container.isActiveToday && container.status != StatusEnum.SUCCESS) {
                    container.status = StatusEnum.ERROR
                }
            }
            if (!failureComment.isNullOrEmpty()) {
                platform.failureComment = failureComment
            }
            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()
            setEntityUpdateAt(platform)
        }
    }

    fun setStateFailureForContainer(platformId: Int, containerId: Int, problem: String, comment: String?=null) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val problemId = findFailReasonByValue(realm, problem).id
            log("FIND CONTAINER NUMBER ${containerId}  problem: ${problemId}")
            containerEntity.failureReasonId = problemId
            if (!comment.isNullOrEmpty()) {
                containerEntity.comment = comment
            }
            containerEntity.status = StatusEnum.ERROR
            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()
            setEntityUpdateAt(platform)
        }
    }

    fun setStateBreakdownForContainer(platformId: Int, containerId: Int, problem: String, comment: String? = null) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val problemId = findBreakdownByValue(realm, problem).id
            log("FIND CONTAINER NUMBER ${containerId} problem: ${problem} problemid: ${problemId}")
            containerEntity.breakdownReasonId = problemId
            if (!comment.isNullOrEmpty()) {
                containerEntity.comment = comment
            }
            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()
            workOrder?.calcInfoStatistics()
            setEntityUpdateAt(platform)
        }
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform().equalTo("platformId", platformId)
                .findFirst()!!

            platform.containers.forEach { container ->
                if (container.volume == null) {
                    if (container.isActiveToday) {
                        container.volume = 1.0
                        if (container.status == StatusEnum.NEW) {
                            container.status = StatusEnum.SUCCESS
                        }
                    }

                }
            }

            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()

            workOrder?.calcInfoStatistics()

            setEntityUpdateAt(platform)
        }
    }

    fun updatePlatformStatusUnfinished(platformId: Int) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform().equalTo("platformId", platformId)
                .findFirst()!!

            val workOrder = getQueryWorkOrder().equalTo("id", platform.workOrderId)
                .findFirst()

            workOrder?.calcInfoStatistics()

            setEntityUpdateAt(platform)
        }
    }

    fun setEmptyImageEntity(platforms: List<PlatformEntity>) {
        platforms.forEach { platform ->
            platform.afterMediaSize = platform.afterMedia.size
            platform.beforeMediaSize = platform.beforeMedia.size
            platform.afterMedia = mEmptyImageEntityList
            platform.beforeMedia = mEmptyImageEntityList
            platform.failureMedia = mEmptyImageEntityList
            platform.pickupMedia = mEmptyImageEntityList
            platform.kgoRemaining?.media = mEmptyImageEntityList
            platform.kgoServed?.media = mEmptyImageEntityList
            platform.containers.forEach { container ->
                container.failureMedia = mEmptyImageEntityList
                container.breakdownMedia = mEmptyImageEntityList
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
            .greaterThanOrEqualTo("coordLat", minLat)
            .lessThanOrEqualTo("coordLat", maxLat)
            .greaterThanOrEqualTo("coordLong", minLong)
            .lessThanOrEqualTo("coordLong", maxLong)
            .findAll()
            .filter { it.getStatusPlatform() == StatusEnum.NEW  }
        if (platformByCoord.isNullOrEmpty()) {
            return res
        }
        if (platformByCoord.size == 1) {
            res = platformByCoord[0]?.let { p_realm.copyFromRealm(it) }
            if (res != null) {
                LoG.warn( "res.address=${res.address} ")
            }
        }
        if (res == null) {
            LoG.warn( "platformByCoord.count=${platformByCoord.size} ")
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
                val platform = _getPlatformEntity_know0(it.platformId!!)
                if (platform.getStatusPlatform() != StatusEnum.NEW && !platform.networkStatus!!) {
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

    fun findContainersSortedByIsActiveToday(platformId: Int): List<ContainerEntity> {
        val platform = p_realm.where(PlatformEntity::class.java)
            .equalTo("platformId", platformId)
            .findFirst()!!
        return p_realm.copyFromRealm(platform.containers.sort("isActiveToday", Sort.DESCENDING))
    }

    fun findContainersVolume(workOrderId: Int): Double {
        var totalKgoVolume = 0.0
        var totalContainersVolume = 0.0
        log("findContainersVolume.before")


        val allContainers = p_realm.copyFromRealm(
            getQueryContainer()
                .equalTo("workOrderId", workOrderId)
                .findAll()
        )
        log("findContainersVolume.totalContainersVolume=${totalContainersVolume}")
        allContainers.forEach { container ->
            container.volume?.let{
                val filledVolume = container.constructiveVolume!! * (container.convertVolumeToPercent() / 100)
                log("findContainersVolume.filledVolume=${filledVolume}")
                totalContainersVolume += filledVolume
                log("findContainersVolume.totalContainersVolume=${totalContainersVolume}")
            }
        }
        log("findContainersVolume.totalContainersVolume=${totalContainersVolume}")

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
        log("findContainersVolume.totalKgoVolume=${totalKgoVolume}")

        val result = totalContainersVolume + totalKgoVolume
        log("findContainersVolume.result=${result}")
        val resultRound = round(result * 100) / 100
        log("findContainersVolume.resultRound=${resultRound}")
        return resultRound
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return p_realm.copyFromRealm(p_realm.createObjectFromJson(clazz, json)!!)
    }

    //todo: private fun _getPlatformEntity
    fun _getPlatformEntity_know0(platformId: Int): PlatformEntity {
        val res = getQueryPlatform()
            .equalTo("platformId", platformId)
            .findFirst()!!
        return res
    }

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val result: PlatformEntity
        if(platformId == Inull) {
            return PlatformEntity(name="findPlatformEntity.platformId==Inull")
        }
        val res = _getPlatformEntity_know0(platformId)
        result = p_realm.copyFromRealm(res)
        return result
    }

    //todo: private fun _getContainerEntity
    fun _getContainerEntity_know0(containerId: Int): ContainerEntity {
        val res = getQueryContainer()
            .equalTo("containerId", containerId)
            .findFirst()!!
        return res
    }

    fun getContainerEntity(containerId: Int): ContainerEntity {
        val result: ContainerEntity
        val res = _getContainerEntity_know0(containerId)
        result = p_realm.copyFromRealm(res)
        return result
    }

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
        val filteredList = result.filter { it.getStatusPlatform() != StatusEnum.NEW && it.beginnedAt != null }
        return filteredList
    }

    fun findPlatformsIsNew(): List<PlatformEntity> {
        val result = p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).findAll().sort("updateAt")
        )
        val filteredList = result.filter { it.getStatusPlatform() == StatusEnum.NEW }
        return filteredList
    }

    fun addBeforeMedia(platformId: Int, imageS: List<ImageEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            if (platformEntity?.beginnedAt == null) {
                platformEntity?.beginnedAt = MyUtil.currentTime()
            }
            platformEntity?.beforeMedia = mEmptyImageEntityList
            platformEntity?.beforeMedia?.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addKgoServed(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.kgoServed?.media = mEmptyImageEntityList
            platformEntity!!.addServerKGOMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformKgoRemaining(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.kgoRemaining?.media = mEmptyImageEntityList
            platformEntity!!.addRemainingKGOMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformPickupMedia(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity!!.pickupMedia = mEmptyImageEntityList
            platformEntity.pickupMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addAfterMedia(platformId: Int, imageS: List<ImageEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.afterMedia = mEmptyImageEntityList
            platformEntity?.afterMedia?.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBeforeMediaSimplifyServe(platformId: Int, imageS: List<ImageEntity>) {
        addBeforeMedia(platformId, imageS)
    }

    fun addAfterMediaSimplifyServe(platformId: Int, imageS: List<ImageEntity>) {
        addAfterMedia(platformId, imageS)
    }

    fun addFailureMediaPlatform(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            if (platformEntity?.beginnedAt == null) {
                platformEntity?.beginnedAt = MyUtil.currentTime()
            }
            platformEntity?.failureMedia = mEmptyImageEntityList
            platformEntity?.failureMedia?.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    /** добавление фото в платформу **/
    fun updatePlatformMedia(imageFor: Int, platformId: Int, imageEntity: ImageEntity) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
//            when (imageFor) {
//              PhotoTypeEnum.forSimplifyServeAfter -> {
//
//                }
//                PhotoTypeEnum.forSimplifyServeBefore -> {
//                    platformEntity?.beforeMedia?.add(imageEntity)
//                }
//            }

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

    fun addFailureMediaContainer(platformId: Int, containerId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.failureMedia = mEmptyImageEntityList
            containerEntity.failureMedia.addAll(imageS)

            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBreakdownMediaContainer(platformId: Int, containerId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.breakdownMedia = mEmptyImageEntityList
            containerEntity.breakdownMedia.addAll(imageS)

            setEntityUpdateAt(platformEntity)
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

//                PhotoTypeEnum.forContainerBreakdown -> {
//
//                }
            }

            setEntityUpdateAt(platformEntity)
        }
    }
    /** удалить фото с контейнера **/
//    fun removeContainerMedia(photoFor: Int,platformId: Int, containerId: Int, imageBase64: ImageEntity) {
//        p_realm.executeTransaction { realm ->
//            val containerEntity = realm.where(ContainerEntity::class.java)
//                .equalTo("containerId", containerId)
//                .findFirst()!!
//            val platformEntity = getQueryPlatform()
//                .equalTo("platformId", platformId)
//                .findFirst()!!
//
//            when (photoFor) {
//                PhotoTypeEnum.forContainerFailure -> {
//                    containerEntity.failureMedia.remove(imageBase64)
//                }
//                PhotoTypeEnum.forContainerBreakdown -> {
//                    containerEntity.breakdownMedia.remove(imageBase64)
//                }
//            }
//            setEntityUpdateAt(platformEntity)
//        }
//    }

    fun getImageList(platformId: Int, containerId: Int, photoFor: Int): List<ImageEntity> {
        val platformEntity = p_realm.where(PlatformEntity::class.java).equalTo("platformId", platformId).findFirst()
        var result = emptyList<ImageEntity>()
//        var md5List: RealmList<ImageEntity>? = null
//        when (photoFor) {
//            PhotoTypeEnum.forBeforeMedia -> {
//                md5List = platformEntity!!.beforeMedia
//            }
//            PhotoTypeEnum.forAfterMedia -> {
//                md5List = platformEntity!!.afterMedia
//            }
//            PhotoTypeEnum.forPlatformProblem -> {
//                md5List = platformEntity!!.failureMedia
//            }
//            PhotoTypeEnum.forPlatformPickupVolume -> {
//                md5List = platformEntity!!.pickupMedia
//            }
//            PhotoTypeEnum.forServedKGO -> {
//                md5List = platformEntity!!.kgoServed!!.media
//            }
//            PhotoTypeEnum.forRemainingKGO -> {
//                md5List = platformEntity!!.kgoRemaining!!.media
//            }
//            PhotoTypeEnum.forContainerFailure -> {
//                val containerEntity = p_realm.where(ContainerEntity::class.java)
//                    .equalTo("containerId", containerId)
//                    .findFirst()!!
//                md5List = containerEntity.failureMedia
//            }
//            PhotoTypeEnum.forContainerBreakdown -> {
//                val containerEntity = p_realm.where(ContainerEntity::class.java)
//                    .equalTo("containerId", containerId)
//                    .findFirst()!!
//                md5List = containerEntity.breakdownMedia
//            }
////            else -> {
////                md5List = emptyList()
////            }
//        }
////        val result = realm.copyFromRealm(realm.where(ImageEntity::class.java).findAll().filter { it.md5 in (md5List) }
////        )
//        if(md5List == null) {
//            return result
//        }
//        result = p_realm.copyFromRealm(md5List)
        return result
    }

    /** удалить фото с платформы **/
    fun removePlatformMedia(imageFor: Int, imageBase64: ImageEntity, platformId: Int) {
//        p_realm.executeTransaction { realm ->
//            val platformEntity = getQueryPlatform()
//                .equalTo("platformId", platformId).findFirst()
//            when (imageFor) {
//                PhotoTypeEnum.forBeforeMedia -> {
//                    platformEntity?.beforeMedia?.remove(imageBase64)
//                }
//
//                PhotoTypeEnum.forAfterMedia -> {
//                    platformEntity?.afterMedia?.remove(imageBase64)
//                }
//                PhotoTypeEnum.forPlatformProblem -> {
//                    platformEntity?.failureMedia?.remove(imageBase64)
//                }
//
//                PhotoTypeEnum.forPlatformPickupVolume -> {
//                    platformEntity!!.pickupMedia.remove(imageBase64)
//                }
//                PhotoTypeEnum.forServedKGO -> {
//                    platformEntity!!.kgoServed!!.media.remove(imageBase64)
//                }
//                PhotoTypeEnum.forRemainingKGO -> {
//                    platformEntity!!.kgoRemaining!!.media.remove(imageBase64)
//                }
//            }
//            setEntityUpdateAt(platformEntity)
//        }
    }

    private fun setEntityUpdateAt(entity: PlatformEntity?) {
        entity?.updateAt = MyUtil.timeStampInSec()
    }

    private fun _changePlatformSCoordinate(platformS: List<PlatformEntity>) {
//        TODO("Not yet implemented")
        //          lat=0,000133755 это 15 метров
        val LAT1M = 0.000008917
        val LONG1M = 0.00001488
        //val alpha: Double = idx * Math.PI /180
        //          long=0,0002232 это 15 метров
        //var koef = 15f + platforms.size
        val koef = 1f
        var stepLat = LAT1M * koef
        var stepLong = LONG1M * koef
        for( idx in 1..platformS.size-1) {
            val platform = platformS[idx]
            val xLat: Double = platform.coordLat + stepLat
            val yLong: Double = platform.coordLong + stepLong
            LoG.warn( "changePlatformSCoordinate.platform.coordLat= ${platform.coordLat}.old")
            LoG.warn( "changePlatformSCoordinate.platform.coordLong= ${platform.coordLong}.old")
            platform.coordLat = xLat
            platform.coordLong = yLong
            LoG.warn( "changePlatformSCoordinate.platform.coordLat= ${platform.coordLat}.new")
            LoG.warn( "changePlatformSCoordinate.platform.coordLong= ${platform.coordLong}.new")
            stepLat += LAT1M * koef
            stepLong += LONG1M * koef
        }
    }

    private fun insUpdWorkOrders(workOrderS: RealmList<WorkOrderEntity>, isInfoChange: Boolean = true) {
        p_realm.executeTransaction { realm ->
            for (workOrder in workOrderS) {
                if (isInfoChange) {
                    workOrder.calcInfoStatistics()
                }
                realm.insertOrUpdate(workOrder)
            }
            val platformS = getQueryPlatform(true).findAll()
            for (platform in platformS) {
                var platformSForChange = _getPlatformSForChange(platform, platformS)
                //todo: count(-ом) ограничить? не зациклиться ли?)
                while (platformSForChange.size > 1) {
                    _changePlatformSCoordinate(platformSForChange)
                    platformSForChange = _getPlatformSForChange(platform, platformS)
                }
            }
            realm.insertOrUpdate(platformS)
        }
//        return workOrder
    }

    private fun _getPlatformSForChange(platform: PlatformEntity, platformS: RealmResults<PlatformEntity>): List<PlatformEntity> {
        val platformSForChange = platformS.filter {
            it.coordLat == platform.coordLat && it.coordLong == platform.coordLong
        }
        return platformSForChange
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
        LoG.info( "setWorkOrderIsShowForUser.before")
        p_realm.executeTransaction { realm ->
            for (workorder in workOrderS) {
                todo_know1(workorder.id, workorder.isShowForUser)
            }
        }
        LoG.warn( "setWorkOrderIsShowForUser.after")
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
        log("hasWorkOrderInProgress.${res}")
        return res
    }

    private fun getQueryPlatform(isForceMode: Boolean = false, platformId: Int? = null): RealmQuery<PlatformEntity> {
        var result:  RealmQuery<PlatformEntity>
        if (isForceMode) {
            result =  p_realm.where(PlatformEntity::class.java)
        } else {
            result = p_realm.where(PlatformEntity::class.java)
                .equalTo("isWorkOrderProgress", true)
                .equalTo("isWorkOrderComplete", false)
        }
        LoG.trace("platformId=${platformId}")
        if (platformId == null) {
            return result
        }
        result = result.equalTo("platformId", platformId)
        return result
    }
    private fun getQueryContainer(): RealmQuery<ContainerEntity> {
        return p_realm.where(ContainerEntity::class.java)
            .equalTo("isWorkOrderProgress", true)
            .equalTo("isWorkOrderComplete", false)
    }

    private fun getQueryGroupByContainerClient(platformId: Int): RealmQuery<GroupByContainerClientEntity> {
        return p_realm.where(GroupByContainerClientEntity::class.java).equalTo("platformId", platformId)
    }


    private fun getQueryGroupByContainerClientType(platformId: Int): RealmQuery<GroupByContainerClientTypeEntity> {
        return p_realm.where(GroupByContainerClientTypeEntity::class.java).equalTo("platformId", platformId)
    }

    private fun getQueryWorkOrder(isForceMode: Boolean = false): RealmQuery<WorkOrderEntity> {
        if (isForceMode) {
            return p_realm.where(WorkOrderEntity::class.java)
        } else {
            return p_realm.where(WorkOrderEntity::class.java).isNotNull("progress_at")
        }
    }

    fun loadConfig(name: ConfigName): ConfigEntity {
        val result: ConfigEntity
        val configEntity = p_realm.where(ConfigEntity::class.java).equalTo("name", name.displayName.uppercase()).findFirst()
        if (configEntity == null) {
           result = ConfigEntity()
           result.configName = name
        } else {
            result = p_realm.copyFromRealm(configEntity)
        }
        return result
    }

    fun saveConfig(configEntity: ConfigEntity) {
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(configEntity)
        }
    }

    fun close() {
        p_realm.close()
    }


    fun loadGroupByContainerClient(platformId: Int): MutableList<GroupByContainerClientEntity>? {
        var result: MutableList<GroupByContainerClientEntity>?  = null
        val realmResult =getQueryGroupByContainerClient(platformId)
                .findAll()
        LoG.trace("realmResult=${realmResult.count()}")
        if (realmResult.isNotEmpty()) {
            result = p_realm.copyFromRealm(realmResult)
        }
        LoG.trace("result=${result?.count()}")
        return result
    }

//    эх.... работа тяжкая))))
    fun createGroupByContainerEntityS(platformId: Int) {
        LoG.trace("platformId=${platformId}")
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform(platformId = platformId).findFirst()

            if (platformEntity == null) {
                LoG.error("platformEntity == null")
                return@executeTransaction
            }
            val containerSSorted = platformEntity.containers
            if (containerSSorted.isEmpty()) {
                LoG.error("containerSSorted.isEmpty()")
                return@executeTransaction
            }
            containerSSorted.sortWith(compareBy{it.client})
            containerSSorted.sortWith(compareBy{it.typeName})


            var clientName: String = Snull

            var groupByContainerClientEntity = GroupByContainerClientEntity.createEmpty()//todo:R_dos!!!
            containerSSorted.forEach { containerSorted ->
                if (clientName == containerSorted.client) {
                    groupByContainerClientEntity.containers.add(containerSorted)
                    return@forEach
                }
                groupByContainerClientEntity = realm.createObject(GroupByContainerClientEntity::class.java)
                groupByContainerClientEntity.platformId = platformId
                if (containerSorted.client != null) {
                    groupByContainerClientEntity.client = containerSorted.client!!
                }
                groupByContainerClientEntity.containers.add(containerSorted)

                realm.insertOrUpdate(groupByContainerClientEntity)
                clientName = containerSorted.client!!
            }


            var groupByContainerClientS = getQueryGroupByContainerClient(platformId).findAll()
            var groupByContainerClientTypeEntity = GroupByContainerClientTypeEntity.createEmpty()//todo:
            var typeName = Snull
            groupByContainerClientS.forEach {
                it.containers.forEach {
                    if (typeName == it.typeName) {
                        groupByContainerClientTypeEntity.containers.add(it)
                        return@forEach
                    }
                    groupByContainerClientTypeEntity = realm.createObject(GroupByContainerClientTypeEntity::class.java)
                    groupByContainerClientTypeEntity.platformId = platformId
                    if (it.client != null) {
                        groupByContainerClientEntity.client = it.client!!
                    }
                    groupByContainerClientTypeEntity.typeId = it.typeId!!
                    groupByContainerClientTypeEntity.typeName = it.typeName!!
                    groupByContainerClientTypeEntity.containers.add(it)

                    realm.insertOrUpdate(groupByContainerClientEntity)
                    typeName = it.typeName!!
                }

            }
        }
    }
}
