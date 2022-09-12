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

    fun insUpdWorkOrderS(woRKoRDeRknow1List: List<WoRKoRDeR_know1>) {
        p_realm.executeTransaction { realm ->
            val workOrderS = WorkOrderEntity.map(woRKoRDeRknow1List, this)
            for (workOrder in workOrderS) {
                workOrder.calcInfoStatistics()
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
            for (platform in platformS) {
                for (container in platform.containerS) {
                    container.platformEntity = platform
                }
            }
            realm.insertOrUpdate(platformS)
        }
//        return workOrder
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
            res = realm.copyFromRealm(workOrderS)
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
            res = realm.copyFromRealm(workOrderS)
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
        LOG.debug("insertFailReason.before ${entities.size}")
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(entities)
        }
        LOG.debug("insertFailReason.after")
    }

    fun insertCancelWayReason(entities: List<CancelWayReasonEntity>) {
        LOG.debug("insertCancelWayReason.before  ${entities.size}")
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

    ////// CopyPaste
    /** добавление заполненности контейнера **/
    fun updateContainerVolume(platformId: Int, containerId: Int, volume: Double?) {
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            container.volume = volume
            setEntityUpdateAt(platformEntity)
        }
    }

    ////// CopyPaste Grow = расти!
    fun setContainerGROUPClientTypeVolume(groupByContainerTypeClient: ContainerGROUPClientTypeEntity, containerId: Int, newVolume: Double) {
        p_realm.executeTransaction { realm ->
            val container = realm.where(ContainerEntity::class.java)
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", groupByContainerTypeClient.platformId)
                .findFirst()
            container.volume = newVolume
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

            val failureReasonId = findFailReasonByValue(realm, problem).id
            platform.failureReasonId = failureReasonId

            platform.containerS.forEach { container ->
                if (container.getStatusContainer() == StatusEnum.NEW) {
                    container.failureReasonId = failureReasonId
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
            LOG.debug("FIND CONTAINER NUMBER ${containerId}  problem: ${problemId}")
            containerEntity.failureReasonId = problemId
            if (!comment.isNullOrEmpty()) {
                containerEntity.comment = comment
            }
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
            LOG.debug("FIND CONTAINER NUMBER ${containerId} problem: ${problem} problemid: ${problemId}")
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

            platform.containerS.forEach { container ->
                if (container.volume == null) {
                    if (container.isActiveToday) {
                        container.volume = 1.0
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
                LOG.warn( "res.address=${res.address} ")
            }
        }
        if (res == null) {
            LOG.warn( "platformByCoord.count=${platformByCoord.size} ")
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
        val TIME_30MIN_SEC = 30 * 60
        //todo: -1 секунда
        val lastSynchroTime = App.getAppParaMS().lastSynchroTimeInSec - 1L
        return p_realm.copyFromRealm(p_realm.where(PlatformEntity::class.java)
            .greaterThan("updateAt", lastSynchroTime)
            .lessThanOrEqualTo("updateAt", lastSynchroTime + TIME_30MIN_SEC)
            .findAll())
    }



    fun updatePlatformNetworkStatus(list: List<PlatformEntity>) {
        p_realm.executeTransaction {
            list.forEach {
                val platform = getQueryPlatform()
                    .equalTo("platformId", it.platformId)
                    .findFirst()!!
                platform.networkStatus = true
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
        return p_realm.copyFromRealm(platform.containerS.sort("isActiveToday", Sort.DESCENDING))
    }

    fun findContainersVolume(workOrderId: Int): Double {
        var totalKgoVolume = 0.0
        var totalContainersVolume = 0.0
        LOG.debug("findContainersVolume.before")


        val allContainers = p_realm.copyFromRealm(
            getQueryContainer()
                .equalTo("workOrderId", workOrderId)
                .findAll()
        )
        LOG.debug("findContainersVolume.totalContainersVolume=${totalContainersVolume}")
        allContainers.forEach { container ->
            container.volume?.let{
                val filledVolume = container.constructiveVolume!! * (container.convertVolumeToPercent() / 100)
                LOG.debug("findContainersVolume.filledVolume=${filledVolume}")
                totalContainersVolume += filledVolume
                LOG.debug("findContainersVolume.totalContainersVolume=${totalContainersVolume}")
            }
        }
        LOG.debug("findContainersVolume.totalContainersVolume=${totalContainersVolume}")

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
        LOG.debug("findContainersVolume.totalKgoVolume=${totalKgoVolume}")

        val result = totalContainersVolume + totalKgoVolume
        LOG.debug("findContainersVolume.result=${result}")
        val resultRound = round(result * 100) / 100
        LOG.debug("findContainersVolume.resultRound=${resultRound}")
        return resultRound
    }

    fun <E : RealmModel?> createObjectFromJson(clazz: Class<E>, json: String): E {
        return p_realm.copyFromRealm(p_realm.createObjectFromJson(clazz, json)!!)
    }

    //todo: private fun _getPlatformEntity
    fun _getPlatformEntity_know0(platformId: Int): PlatformEntity {
        var res = PlatformEntity(name="findPlatformEntity.platformId==Inull")
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()

            if(platform == null)
                return@executeTransaction

            val configEntities = realm.copyFromRealm(realm.where(ConfigEntity::class.java).equalTo("isAppEvent", true).findAll()).toMutableList()
            platform.events = RealmList()
            platform.events.addAll(configEntities.map { el ->
                AppEventEntity(event = el.configName.displayName, counter = el.value)
            })
            res = platform
        }
        return res
    }

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val result: PlatformEntity
        if(platformId == Inull) {
            return PlatformEntity(name="findPlatformEntity.platformId==Inull")
        }
        val res = _getPlatformEntity_know0(platformId)
        result = if(res.isValid)
            p_realm.copyFromRealm(res)
        else
            res
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

    fun addVoiceComment(platformVoiceCommentEntity: PlatformVoiceCommentEntity) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformVoiceCommentEntity.platformId)
                .findFirst()

            val platformVoiceComment = loadPlatformVoiceCommentEntity(platformEntity!!)
            platformVoiceComment.voiceByteArray = platformVoiceCommentEntity.voiceByteArray
            platformVoiceComment.updateAd = MyUtil.currentTime()
            platformEntity.platformVoiceCommentEntity = platformVoiceComment
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBeforeMedia(platformId: Int, imageS: List<ImageEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            if (platformEntity?.beginnedAt == null) {
                platformEntity?.beginnedAt = MyUtil.currentTime()
            }
            platformEntity?.addBeforeMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity!!)
            platformMediaEntity.beforeMedia = mEmptyImageEntityList
            platformMediaEntity.beforeMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun getPlatformVoiceCommentEntity(platformEntity: PlatformEntity): PlatformVoiceCommentEntity {
        var result: PlatformVoiceCommentEntity = PlatformVoiceCommentEntity.createEmpty()
        p_realm.executeTransaction { realm ->
            val platformVoiceCommentEntity = loadPlatformVoiceCommentEntity(platformEntity)
            result = realm.copyFromRealm(platformVoiceCommentEntity)
        }
        return result
    }
    private fun loadPlatformVoiceCommentEntity(platformEntity: PlatformEntity): PlatformVoiceCommentEntity {
        var result = getQueryPlatformVoiceComment(platformEntity).findFirst()
        if (result == null) {
            result = p_realm.createObject(PlatformVoiceCommentEntity::class.java,  platformEntity.platformId)
            val platform = getQueryPlatform()
                .equalTo("platformId", platformEntity.platformId)
                .findFirst()
            result.platformEntity = platform
        }
        return result!!
    }

    fun getPlatformMediaEntity(platformEntity: PlatformEntity): PlatformMediaEntity {
        var result: PlatformMediaEntity = PlatformMediaEntity.createEmpty()
        p_realm.executeTransaction { realm ->
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity)
            result = realm.copyFromRealm(platformMediaEntity)
        }

        return result
    }
    fun loadPlatformMediaEntity(platformEntity: PlatformEntity): PlatformMediaEntity {
        var result = getQueryPlatformMedia(platformEntity).findFirst()
        if (result == null) {
            result = p_realm.createObject(PlatformMediaEntity::class.java,  platformEntity.platformId)
            result.workOrderId = platformEntity.workOrderId
        }
        return result!!
    }

    fun getContainerMediaEntity(containerEntity: ContainerEntity): ContainerMediaEntity {
        var result: ContainerMediaEntity = ContainerMediaEntity.createEmpty()
        p_realm.executeTransaction { realm ->
            val containerMediaEntity = loadContainerMediaEntity(containerEntity)
            result = realm.copyFromRealm(containerMediaEntity)
        }

        return result
    }
    fun loadContainerMediaEntity(containerEntity: ContainerEntity): ContainerMediaEntity {
        var result = getQueryContainerMedia(containerEntity).findFirst()
        if (result == null) {
            result = p_realm.createObject(ContainerMediaEntity::class.java, containerEntity.containerId)
//            val platform = getQueryPlatform()
//                .equalTo("platformId", platformEntity.platformId)
//                .findFirst()
//            result!!.platformEntity = platform
//            val container = getQueryContainer()
//                .equalTo("containerId", containerEntity.containerId)
//                .findFirst()
            result.platformId = containerEntity.platformId
            result.workOrderId = containerEntity.workOrderId
        }
        return result!!
    }

    fun addKgoServed(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()

            platformEntity!!.addServerKGOMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity)
            platformMediaEntity.kgoServedMedia = mEmptyImageEntityList
            platformMediaEntity.kgoServedMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformKgoRemaining(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity!!.addRemainingKGOMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity)
            platformMediaEntity.kgoRemainingMedia = mEmptyImageEntityList
            platformMediaEntity.kgoRemainingMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformPickupMedia(platformId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.addPickupMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity!!)
            platformMediaEntity.pickupMedia = mEmptyImageEntityList
            platformMediaEntity.pickupMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addAfterMedia(platformId: Int, imageS: List<ImageEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.addAfterMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity!!)
            platformMediaEntity.afterMedia = mEmptyImageEntityList
            platformMediaEntity.afterMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBeforeMediaComntainerByTypes(platformId: Int, imageS: List<ImageEntity>) {
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
            platformEntity?.addFailureMedia(imageS)
            val platformMediaEntity = loadPlatformMediaEntity(platformEntity!!)
            platformMediaEntity.failureMedia = mEmptyImageEntityList
            platformMediaEntity.failureMedia.addAll(imageS)
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

    fun addFailureMediaContainer(platformId: Int, containerId: Int, imageS: List<ImageEntity>) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.addFailureMedia(imageS)

            val containerMediaEntity = loadContainerMediaEntity(containerEntity)
            containerMediaEntity.failureMedia = mEmptyImageEntityList
            containerMediaEntity.failureMedia.addAll(imageS)

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
            containerEntity.addBreakdown(imageS)
            val containerMediaEntity = loadContainerMediaEntity(containerEntity)
            containerMediaEntity.breakdownMedia = mEmptyImageEntityList
            containerMediaEntity.breakdownMedia.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
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
            LOG.warn( "changePlatformSCoordinate.platform.coordLat= ${platform.coordLat}.old")
            LOG.warn( "changePlatformSCoordinate.platform.coordLong= ${platform.coordLong}.old")
            platform.coordLat = xLat
            platform.coordLong = yLong
            LOG.warn( "changePlatformSCoordinate.platform.coordLat= ${platform.coordLat}.new")
            LOG.warn( "changePlatformSCoordinate.platform.coordLong= ${platform.coordLong}.new")
            stepLat += LAT1M * koef
            stepLong += LONG1M * koef
        }
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
        LOG.info( "setWorkOrderIsShowForUser.before")
        p_realm.executeTransaction { realm ->
            for (workorder in workOrderS) {
                todo_know1(workorder.id, workorder.isShowForUser)
            }
        }
        LOG.warn( "setWorkOrderIsShowForUser.after")
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
                for (container in platform.containerS) {
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
                for (container in platform.containerS) {
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
        LOG.debug("hasWorkOrderInProgress.${res}")
        return res
    }

    private fun getQueryPlatformMedia(platformEntity: PlatformEntity): RealmQuery<PlatformMediaEntity> {
        return p_realm.where(PlatformMediaEntity::class.java).equalTo("platformId", platformEntity.platformId)
    }

    private fun getQueryPlatformVoiceComment(platformEntity: PlatformEntity): RealmQuery<PlatformVoiceCommentEntity> {
        return p_realm.where(PlatformVoiceCommentEntity::class.java).equalTo("platformId", platformEntity.platformId)
    }

    private fun getQueryContainerMedia(containerEntity: ContainerEntity): RealmQuery<ContainerMediaEntity> {
        return p_realm.where(ContainerMediaEntity::class.java).equalTo("containerId", containerEntity.containerId)
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
        LOG.trace("platformId=${platformId}")
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

    private fun getQueryGroupByContainerClient(platformId: Int): RealmQuery<ContainerGROUPClientEntity> {
        return p_realm.where(ContainerGROUPClientEntity::class.java).equalTo("platformId", platformId)
    }


    private fun getQueryGroupByContainerClientType(platformId: Int): RealmQuery<ContainerGROUPClientTypeEntity> {
        return p_realm.where(ContainerGROUPClientTypeEntity::class.java).equalTo("platformId", platformId)
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
            when(name) {
                ConfigName.AIRPLANE_MODE_ON_CNT,
                ConfigName.AIRPLANE_MODE_OFF_CNT,
                ConfigName.NOINTERNET_CNT,
                ConfigName.BOOT_CNT,
                ConfigName.RUNAPP_CNT ,
                ConfigName.SWIPE_CNT -> result.isAppEvent = true
                else -> {}
            }
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


    // TODO::RENAME loadContsGroByClientS !!?
    fun loadGroupByContainerClient(platformId: Int): MutableList<ContainerGROUPClientEntity>? {
        var result: MutableList<ContainerGROUPClientEntity>?  = null
        val realmResult = getQueryGroupByContainerClient(platformId).findAll()

        LOG.trace("realmResult=${realmResult.count()}")
        if (realmResult.isNotEmpty()) {
            result = p_realm.copyFromRealm(realmResult)
        }
        LOG.trace("result=${result?.count()}")
        return result
    }

    fun loadContainerGROUPClientTypeEntity(platformId: Int, typeId: Int, client: String?): ContainerGROUPClientTypeEntity {
        val realmResult = getQueryGroupByContainerClientType(platformId)
            .equalTo("client", client)
            .equalTo("typeId", typeId)
            .findFirst()!!
        return p_realm.copyFromRealm(realmResult)
    }

    // TODO::RENAME loadContsGroByClientNTypeS !!?
    fun loadContainerGROUPClientTypeEntityS(platformId: Int, client: String?): MutableList<ContainerGROUPClientTypeEntity>? {
        var result: MutableList<ContainerGROUPClientTypeEntity>?  = null
        val realmResult = getQueryGroupByContainerClientType(platformId).equalTo("client", client).findAll()
        LOG.trace("realmResult=${realmResult.count()}")
        if (realmResult.isNotEmpty()) {
            result = p_realm.copyFromRealm(realmResult)
        }
        LOG.trace("result=${result?.count()}")
        return result
    }

//    эх.... работа тяжкая))))
    fun createGroupByContainerEntityS(platformId: Int) {
//   todo: эх.... работа тяжкая:(2

//        if (isNotCheckedCreate(platformId)) {
//            return
//        }

        LOG.trace("platformId=${platformId}")
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform(platformId = platformId).findFirst()

            if (platformEntity == null) {
                LOG.error("platformEntity == null")
                return@executeTransaction
            }
            val containerS = platformEntity.containerS.filterTo(RealmList(), { it.isActiveToday })
            if (containerS.isEmpty()) {
                LOG.error("containerSSorted.isEmpty()")
                return@executeTransaction
            }
            LOG.debug("containerS.count = ${containerS.count()}")
            for(container in containerS) {
                LOG.warn("containerSBeforeSortWith::cont.number=${container.number}" + "cont.client=${container.client}" + "cont.typeName=${container.typeName}")
            }
            containerS.sortWith(compareBy({ it.client }, { it.typeName }))
            for(container in containerS) {
                LOG.trace("containerSAfterSortWith::cont.number=${container.number}" + "cont.client=${container.client}" + "cont.typeName=${container.typeName}")
            }

            LOG.debug("create:GroupByContainerClientEntity::before")
            var clientName: String? = Snull

                                                                                    //            val groupByContainerClient = this.loadGroupByContainerClient(platformId)
                                                                                    //            if (groupByContainerClient != null) {
                                                                                    //
                                                                                    //            }
//                                                                              todo: WTF????????????????????????
            var containerGROUPClientEntity = ContainerGROUPClientEntity.createEmpty()//todo:R_dos!!!
            for(cont in containerS) {
                if (clientName == cont.client) {
                    containerGROUPClientEntity.containers.add(cont)
                    continue
                }
                containerGROUPClientEntity = realm.createObject(ContainerGROUPClientEntity::class.java)
                containerGROUPClientEntity.platformId = platformId
                containerGROUPClientEntity.addClient(cont)

                containerGROUPClientEntity.containers.add(cont)

//   todo: КТО ГДЕ КОГДА! r_dos??                    realm.insertOrUpdate(groupByContainerClientEntity)
                clientName = cont.client
            }
            LOG.debug("create:GroupByContainerClientEntity::after")

            val groupByContainerClientS = getQueryGroupByContainerClient(platformId).findAll()
            LOG.info("groupByContainerClientS.size = ${groupByContainerClientS.size}")

            var containerGROUPClientTypeEntity = ContainerGROUPClientTypeEntity.createEmpty()//todo:
            for(groupByContainerClient in groupByContainerClientS){
                var typeName: String? = Snull
                LOG.debug("groupByContainerClient.client = ${groupByContainerClient.client}")
                for(groupByContainerClientContainer in groupByContainerClient.containers){
                    if (typeName == groupByContainerClientContainer.typeName) {
                        LOG.debug("typeName=${typeName}")
                        LOG.debug("groupByContainerTypeClientEntity.containers.add")
                        containerGROUPClientTypeEntity.containers.add(groupByContainerClientContainer)
                        continue
                    }
                    containerGROUPClientTypeEntity = realm.createObject(ContainerGROUPClientTypeEntity::class.java)
                    containerGROUPClientTypeEntity.platformId = platformId
                    containerGROUPClientTypeEntity.client = groupByContainerClient.client
                    containerGROUPClientTypeEntity.typeId = groupByContainerClientContainer.typeId
                    containerGROUPClientTypeEntity.typeName = groupByContainerClientContainer.typeName
                    containerGROUPClientTypeEntity.containers.add(groupByContainerClientContainer)

//   todo: КТО ГДЕ КОГДА! r_dos??                 realm.insertOrUpdate(groupByContainerClientEntity)
                    typeName = groupByContainerClientContainer.typeName
                }

            }
        }
    }

    private fun isNotCheckedCreate(platformId: Int): Boolean {
        var result = false


//        val groupByContainerTypeClient = this.loadGroupByContainerTypeClientEntity(platformId)
//        if (groupByContainerClient == null) {
//            return result
//        }

        result = true
        return result
    }

}
