package ru.smartro.worknote.work.work

import io.realm.*
import ru.smartro.worknote.*
import ru.smartro.worknote.LiveRealmData
import ru.smartro.worknote.log.todo.*
import ru.smartro.worknote.presentation.SynchroOidWidOutBodyDataWorkorder
import kotlin.math.round


class RealmRepository(private val p_realm: Realm) {
    private val TAG: String = "RealmRepository"
    

    // TODO: ][3
    private val mEmptyImageInfoList = RealmList<ImageInfoEntity>()
    fun insUpdWorkOrderS(woRKoRDeRknow1List: List<SynchroOidWidOutBodyDataWorkorder>) {
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

            //setProgressData
            val workOrderOrders = this.getQueryWorkOrder(true).findAll()
            for (workOrder in workOrderOrders) {
                workOrder.progress_at = App.getAppliCation().currentTime()
                for (platform in workOrder.platforms) {
                    platform.isWorkOrderProgress = true
                    for (container in platform.containerS) {
                        container.isWorkOrderProgress = true
                    }
                }
            }
        }
//        return workOrder
    }

    fun <T:RealmObject> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)

    fun findPlatformsLive(): LiveRealmData<PlatformEntity> {
        return LiveRealmData(getQueryPlatform().sort("updateAt").findAllAsync())
    }

    fun updateImageInfoEntityAttempt(imageInfoEntity: ImageInfoEntity){
        p_realm.executeTransaction {
            val imageInfo = it.where(ImageInfoEntity::class.java).equalTo("md5", imageInfoEntity.md5).findFirst()
            val timestamp = System.currentTimeMillis()
            imageInfo?.synchroAttempt = timestamp
            if(imageInfo != null)
                it.insertOrUpdate(imageInfo)
        }
    }

    fun getImagesToSynchro(): List<ImageInfoEntity> {
        var result: List<ImageInfoEntity> = listOf()
        p_realm.executeTransaction {
            val imageS = it.where(ImageInfoEntity::class.java).findAll()
            result = it.copyFromRealm(imageS)
            result = result.filter { el -> el.synchroTime <= el.synchroAttempt }
        }
        return result
    }

    fun updateImageInfoEntitySynchro(imageInfoEntity: ImageInfoEntity){
        p_realm.executeTransaction {
            val imageInfo = it.where(ImageInfoEntity::class.java).equalTo("md5", imageInfoEntity.md5).findFirst()
            val timestamp = System.currentTimeMillis()
            imageInfo?.synchroAttempt = timestamp
            imageInfo?.synchroTime = timestamp
            if(imageInfo != null)
                it.insertOrUpdate(imageInfo)
        }
    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //    fun findPlatforms(): List<PlatformEntity> {

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
            val realmConfiguration = realm.configuration
            for (clazz in realmConfiguration.realmObjectClasses) {
                if (clazz != RegionEntity::class.java) {
                    realm.delete(clazz)
                }
            }
        }
    }


    fun insertBreakDown(entities: List<BreakDownReasonEntity>) {
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


    private fun findBreakdownByValue(realm: Realm, problem: String): BreakDownReasonEntity {
        return realm.copyFromRealm(
            realm.where(BreakDownReasonEntity::class.java).equalTo("problem", problem).findFirst()!!
        )
    }

    private fun findFailReasonByValue(realm: Realm, problem: String): FailReasonEntity {
        return realm.where(FailReasonEntity::class.java).equalTo("problem", problem).findFirst()!!

    }

    fun findAllFailReason(): List<String> {
        val found = p_realm.copyFromRealm(p_realm.where(FailReasonEntity::class.java).findAll())
        return found.map { it.problem!! }
    }

    fun findAllBreakDownReasonS(): List<String> {
        val found = p_realm.copyFromRealm(p_realm.where(BreakDownReasonEntity::class.java).findAll())
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

    fun updatePlatformComment(platformId: Int, comment: String) {
        p_realm.executeTransaction { realm ->
            val platform = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            platform.comment = comment
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
            .filter { it.getStatusPlatform() == StatusEnum.NEW }
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

    fun findPlatformByCoord(coordLat: Double, coordLong: Double): PlatformEntity? {
        val platformByCoord = getQueryPlatform()
            .equalTo("coordLat", coordLat)
            .equalTo("coordLong", coordLong)
            .findFirst()

        if (platformByCoord == null)
            return platformByCoord

        LOG.warn( "res.address=${platformByCoord.address} ")

        return platformByCoord
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
        LOG.debug("::: findLastPlatforms")
        refreshRealm_know0()
        //todo: -1 секунда
        val lastSynchroTime = App.getAppParaMS().lastSynchroAttemptTimeInSec - 1L
        return p_realm.copyFromRealm(
            p_realm.where(PlatformEntity::class.java).greaterThan("updateAt", lastSynchroTime)
                .findAll()
        )
    }

    fun findPlatforms30min(): List<PlatformEntity> {
        LOG.debug("::: findPlatforms30min")
        refreshRealm_know0()
        val TIME_30MIN_SEC = 30 * 60
        //todo: -1 секунда
        val lastSynchroTime = App.getAppParaMS().lastSynchroAttemptTimeInSec - 1L
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
            platformVoiceComment.updateAd = App.getAppliCation().currentTime()
            platformEntity.platformVoiceCommentEntity = platformVoiceComment
            setEntityUpdateAt(platformEntity)
        }
    }

    fun removeVoiceComment(platformVoiceCommentEntity: PlatformVoiceCommentEntity) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformVoiceCommentEntity.platformId)
                .findFirst()

            platformEntity!!.platformVoiceCommentEntity!!.deleteFromRealm()
            platformEntity.platformVoiceCommentEntity = null


            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBeforeMedia(platformId: Int, imageS: List<ImageInfoEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            if (platformEntity?.beginnedAt == null) {
                platformEntity?.beginnedAt = App.getAppliCation().currentTime()
            }
            platformEntity?.addBeforeMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }


    fun addBeforeMediaUnload(platformId: Int, imageS: List<ImageInfoEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.unloadEntity?.beforeMedia = mEmptyImageInfoList
            platformEntity?.unloadEntity?.beforeMedia?.addAll(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addAfterMediaUnload(platformId: Int, imageS: List<ImageInfoEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.unloadEntity?.afterMedia = mEmptyImageInfoList
            platformEntity?.unloadEntity?.afterMedia?.addAll(imageS)
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

    fun getRegions(): List<RegionEntity> {
        var regions = listOf<RegionEntity>()
        p_realm.executeTransaction { realm ->
            val rawResponse = realm.where(RegionEntity::class.java).isNotNull("showName").findAll()
            regions = realm.copyFromRealm(rawResponse)
        }
        return regions
    }


    /** private*/fun loadPlatformUnloadEntity(platformEntity: PlatformEntity): PlatformUnloadEntity {
        var result = getQueryPlatformUnload(platformEntity).findFirst()
        if (result == null) {
            result = p_realm.createObject(PlatformUnloadEntity::class.java,  platformEntity.platformId)
            result.workOrderId = platformEntity.workOrderId
        }
        return result!!
    }


    fun addKgoServed(platformId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()

            platformEntity!!.addServerKGOMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformKgoRemaining(platformId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity!!.addRemainingKGOMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addPlatformPickupMedia(platformId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.addPickupMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun setPlatformUnloadEntity(newUnloadEntity: PlatformUnloadEntity) {
        p_realm.executeTransaction { realm ->
            //todo: [pe
            val pe = getQueryPlatform()
                .equalTo("platformId", newUnloadEntity.platformId)
                .findFirst()!!
            val platformUnloadEntity = loadPlatformUnloadEntity(pe)

            platformUnloadEntity.afterValue = newUnloadEntity?.afterValue
            platformUnloadEntity.beforeValue = newUnloadEntity?.beforeValue
            platformUnloadEntity.ticketValue = newUnloadEntity?.ticketValue
            setEntityUpdateAt(pe)
        }
    }



    fun addPlatformUnloadEntity(platformEntity: PlatformEntity){
        val newUnloadEntity = platformEntity.unloadEntity
        p_realm.executeTransaction { realm ->
            //todo: [pe
            val pe = getQueryPlatform()
                .equalTo("platformId", platformEntity.platformId)
                .findFirst()!!
            val platformUnloadEntity = loadPlatformUnloadEntity(platformEntity)

            pe?.unloadEntity = platformUnloadEntity
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addAfterMedia(platformId: Int, imageS: List<ImageInfoEntity>/**, isRequireClean: Boolean*/) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            platformEntity?.addAfterMedia(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBeforeMediaComntainerByTypes(platformId: Int, imageS: List<ImageInfoEntity>) {
        addBeforeMedia(platformId, imageS)
    }

    fun addAfterMediaSimplifyServe(platformId: Int, imageS: List<ImageInfoEntity>) {
        addAfterMedia(platformId, imageS)
    }

    fun addFailureMediaPlatform(platformId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()
            if (platformEntity?.beginnedAt == null) {
                platformEntity?.beginnedAt = App.getAppliCation().currentTime()
            }
            platformEntity?.addFailureMedia(imageS)
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

    fun addFailureMediaContainer(platformId: Int, containerId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.addFailureMedia(imageS)

            setEntityUpdateAt(platformEntity)
        }
    }

    fun addBreakdownMediaContainer(platformId: Int, containerId: Int, imageS: List<ImageInfoEntity>) {
        p_realm.executeTransaction { realm ->
            val containerEntity = getQueryContainer()
                .equalTo("containerId", containerId)
                .findFirst()!!
            val platformEntity = getQueryPlatform()
                .equalTo("platformId", platformId)
                .findFirst()!!
            containerEntity.addBreakdown(imageS)
            setEntityUpdateAt(platformEntity)
        }
    }


    private fun setEntityUpdateAt(entity: PlatformEntity?) {
        LOG.debug("before")
        entity?.updateAt = App.getAppliCation().timeStampInSec()
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


    fun setCompleteWorkOrderData(oldWorkOrder: WorkOrderEntity): WorkOrderEntity {
        var result: WorkOrderEntity = oldWorkOrder
        p_realm.executeTransaction { realm ->
            val workOrder = getQueryWorkOrder().equalTo("id", oldWorkOrder.id).findFirst()!!
            workOrder.end_at = App.getAppliCation().currentTime()
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

    private fun getQueryOrganisation(): RealmQuery<OrganisationEntity> {
        return p_realm.where(OrganisationEntity::class.java)
    }

    private fun getQueryVehicle(): RealmQuery<VehicleEntity> {
        return p_realm.where(VehicleEntity::class.java)
    }

    private fun getQueryWaybill(): RealmQuery<WaybillEntity> {
        return p_realm.where(WaybillEntity::class.java)
    }

    private fun getQueryPlatformUnload(platformEntity: PlatformEntity): RealmQuery<PlatformUnloadEntity> {
        return p_realm.where(PlatformUnloadEntity::class.java).equalTo("platformId", platformEntity.platformId)
    }

    private fun getQueryPlatformVoiceComment(platformEntity: PlatformEntity): RealmQuery<PlatformVoiceCommentEntity> {
        return p_realm.where(PlatformVoiceCommentEntity::class.java).equalTo("platformId", platformEntity.platformId)
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

    private fun loadConfig(name: ConfigName): ConfigEntity {
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
                ConfigName.RUNAPP_CNT,
                ConfigName.SWIPE_CNT -> result.isAppEvent = true
                else -> {}
            }
        } else {
            result = p_realm.copyFromRealm(configEntity)
        }
        return result
    }

    private fun saveConfig(configEntity: ConfigEntity) {
        p_realm.executeTransaction { realm ->
            realm.insertOrUpdate(configEntity)
        }
    }

    fun setConfigCntPlusOne(name: ConfigName) {
        tryCatch(){
            val configEntity = loadConfig(name)
            configEntity.cntPlusOne()
            saveConfig(configEntity)
        }

    }
    fun setConfig(name: ConfigName, value: String) {
        val configEntity = loadConfig(name)

        configEntity.value = value.toString()
        saveConfig(configEntity)
    }

    fun setConfig(name: ConfigName, value: Int) {
        val configEntity = loadConfig(name)

        configEntity.value = value.toString()
        saveConfig(configEntity)
    }


    fun setConfig(name: ConfigName, value: Long) {
        val configEntity = loadConfig(name)

        configEntity.value = value.toString()
        saveConfig(configEntity)
    }

    fun setConfig(name: ConfigName, value: Boolean) {
        val configEntity = loadConfig(name)
        configEntity.value = value.toString()
        saveConfig(configEntity)
    }


    fun getConfigBool(name: ConfigName): Boolean {
        var result = false
        val configEntity = loadConfig(name)
        try {
            result = configEntity.value.toBoolean()
        } catch (ex: Exception) {
            configEntity.value = result.toString()
            saveConfig(configEntity)
        }
        return result
    }

    fun getConfigString(name: ConfigName): String {
        val configEntity = loadConfig(name)
        return configEntity.value
    }

    fun getConfigInt(name: ConfigName): Int {
        var result = Inull
        val configEntity = loadConfig(name)
        try {
            result = configEntity.value.toInt()
        } catch (ex: Exception) {
            configEntity.value = result.toString()
            saveConfig(configEntity)
        }
        return result
    }

    fun close() {
        tryCatch(){
            p_realm.close()
        }
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


    fun setOrganisationEntity(entities: List<OrganisationEntity>) {
        LOG.debug("before.entities.size=${entities.size}")
        p_realm.executeTransaction { realm ->
            val organisationEntity = getQueryOrganisation().findAll()
            organisationEntity.deleteAllFromRealm()
            realm.insertOrUpdate(entities)
        }
        LOG.debug("after")
    }


    fun getOrganisationS(): List<OrganisationEntity> {
        var result = emptyList<OrganisationEntity>()
        p_realm.executeTransaction { realm ->
            val organisationEntity = getQueryOrganisation().findAll()
            result = realm.copyFromRealm(organisationEntity)

        }
        return result
    }

    fun setVehicleEntity(entities: List<VehicleEntity>) {
        LOG.debug("before.entities.size=${entities.size}")
        p_realm.executeTransaction { realm ->
            val vehicleS= getQueryVehicle().findAll()
            vehicleS.deleteAllFromRealm()
            realm.insertOrUpdate(entities)
        }
        LOG.debug("after")
    }


    fun getVehicleS(): List<VehicleEntity> {
        var result = emptyList<VehicleEntity>()
        p_realm.executeTransaction { realm ->
            val vehicleS = getQueryVehicle().findAll()
            result = realm.copyFromRealm(vehicleS)

        }
        return result
    }

    fun setWaybillEntity(entities: List<WaybillEntity>) {
        LOG.debug("before.entities.size=${entities.size}")
        p_realm.executeTransaction { realm ->
            val waybillS = getQueryWaybill().findAll()
            waybillS.deleteAllFromRealm()
            realm.insertOrUpdate(entities)
        }
        LOG.debug("after")
    }

    fun getWaybillS(): List<WaybillEntity>{
        var result = emptyList<WaybillEntity>()
        p_realm.executeTransaction { realm ->
            val waybillS = getQueryWaybill().findAll()
            result = realm.copyFromRealm(waybillS)

        }
        return result
    }

    fun setCompleteEarly(entity: WorkOrderEntity): WorkOrderEntity {
        var res = WorkOrderEntity(name="fun setCompleteEarly(workOrderEntity: WorkOrderEntity) {")
            p_realm.executeTransaction { realm ->
                val workOrderEntity = getQueryWorkOrder(true).equalTo("id", entity.id).findFirst()

                if(workOrderEntity == null)
                    return@executeTransaction
                workOrderEntity.failure_id = entity.failure_id
                workOrderEntity.finished_at = entity.finished_at
                workOrderEntity.unload_type = entity.unload_type
                workOrderEntity.unload_value = entity.unload_value
                res = workOrderEntity
            }
        return res
    }

    fun removeImageInfoEntityByHash(hash: String) {
        p_realm.executeTransaction { realm ->
            val imageInfoEntity = realm.where(ImageInfoEntity::class.java)
                .equalTo("md5", hash)
                .findFirst()

            imageInfoEntity?.deleteFromRealm()
        }
    }

}
