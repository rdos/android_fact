package ru.smartro.worknote.presentation.platform_serve

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.Dnull
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.*

class ServePlatformVM(app: Application) : AViewModel(app) {
//   dIPlatformId
    private var mPlatformId: Int = Inull

    private var mPlatformEntity: PlatformEntity? = null
    private var mPlatformMediaEntity: PlatformMediaEntity? = null
    private var mPlatformVoiceCommentEntity: PlatformVoiceCommentEntity? = null

    private val _PlatformLiveData: MutableLiveData<PlatformEntity> = MutableLiveData(PlatformEntity())
    val todoLiveData: LiveData<PlatformEntity>
        get() = _PlatformLiveData


    private var mContainerGROUPClientEntity: MutableList<ContainerGROUPClientEntity>? = null
    private var mContainerGROUPClientTypeEntity: MutableList<ContainerGROUPClientTypeEntity>? = null

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
//    val mFailReasonS: LiveData<List<String>>
//        get() = _failReasonS


    fun getPlatformEntity(): PlatformEntity {
        if (mPlatformEntity == null) {
            mPlatformEntity = database.getPlatformEntity(this.getPlatformId())
            LOG.trace("result=${mPlatformEntity?.platformId}")
        }
        return mPlatformEntity!!
    }

    fun updatePlatformEntity() {
        if(this.getPlatformId() != Inull && mPlatformEntity != null) {
            set_PlatformLiveData()
        }
    }

    fun getPlatformMediaEntity(): PlatformMediaEntity {
        if (mPlatformMediaEntity == null) {
            mPlatformMediaEntity = database.getPlatformMediaEntity(getPlatformEntity())
            LOG.trace("result.getBeforeMediaSize=${mPlatformMediaEntity!!.getBeforeMediaSize()}")
        }
        return mPlatformMediaEntity!!
    }

    fun getPlatformVoiceCommentEntity(): PlatformVoiceCommentEntity {
        if (mPlatformVoiceCommentEntity == null) {
            mPlatformVoiceCommentEntity = database.getPlatformVoiceCommentEntity(getPlatformEntity())
            LOG.trace("result.getBeforeMediaSize=${mPlatformMediaEntity!!.getBeforeMediaSize()}")
        }
        return mPlatformVoiceCommentEntity!!
    }

    fun getPlatformId(): Int {
        if (mPlatformId == Inull) {
            LOG.warn("(mPlatformId == Inull")
            mPlatformId = database.getConfigInt(ConfigName.AAPP__LAST_PLATFORM_ID)
            return mPlatformId
        }
        LOG.trace("dIPlatformId=${mPlatformId}")
        return mPlatformId
    }

    fun loadPlatformEntityByCoordS(coordLat: Double, coordLong: Double): PlatformEntity {
        val platformE = database.findPlatformByCoord(coordLat, coordLong)
        if(platformE == null)
            mPlatformId = Inull
        else
            mPlatformId = platformE.platformId
        set_PlatformLiveData()
        return platformE ?: PlatformEntity()
    }

    // TODO: !!!
    fun setPlatformEntity(platformEntity: PlatformEntity) {
        LOG.debug("before.mPlatformId=${mPlatformId}")
        LOG.debug("before.platformEntity.platformId=${platformEntity.platformId}")

//        mPlatformEntity = platformEntity
        if (getPlatformId() == platformEntity.platformId) {
            LOG.warn("mPlatformId == platformEntity.platformId")
            return
        }
        mPlatformId = platformEntity.platformId
        database.setConfig(ConfigName.AAPP__LAST_PLATFORM_ID, mPlatformId)
        set_PlatformLiveData()
        mPlatformMediaEntity = null
        mContainerGROUPClientEntity = null
        mContainerGROUPClientTypeEntity = null
        LOG.trace("after.mPlatformId=${mPlatformId}")
    }

    fun getContainerS(): List<ContainerEntity> {
        var result =  this.getPlatformEntity().containerS.toList()

        result = result.sortedBy {
            it.isActiveToday == false
        }
        LOG.trace("result=${result.count()}")
        return result
    }

    fun getContainer(containerId: Int): ContainerEntity {
        LOG.trace("containerId=${containerId}")
        var result =  this.getPlatformEntity().containerS.find { it.containerId == containerId}
        if (result == null) {
           result = ContainerEntity.createEmpty()
        }
        LOG.trace("result.isFailureNotEmpty() = ${result.isFailureNotEmpty()}")
        LOG.debug("result = ${result.containerId}")
        return result
    }

    fun getContainerMediaEntity(containerId: Int): ContainerMediaEntity {
        val result = database.getContainerMediaEntity(getContainer(containerId))
        return result
    }


    // TODO: !??
    fun getGroupByContainerClientS(): MutableList<ContainerGROUPClientEntity> {
        // todo:: !!!!
//        if (mGroupByContainerClientEntity == null) {
            mContainerGROUPClientEntity = database.loadGroupByContainerClient(this.getPlatformId())
            LOG.info("mGroupByContainerClientEntity START = ${mContainerGROUPClientEntity}")
            if (mContainerGROUPClientEntity == null) {
                database.createGroupByContainerEntityS(this.getPlatformId())
                mContainerGROUPClientEntity = database.loadGroupByContainerClient(this.getPlatformId())
                LOG.info("mGroupByContainerClientEntity NULL 1 = ${mContainerGROUPClientEntity}")
            }
            if (mContainerGROUPClientEntity == null) {
                LOG.error("mGroupByContainerClientEntity == null")
                mContainerGROUPClientEntity = mutableListOf(ContainerGROUPClientEntity.createEmpty())
            }
//        }
        return mContainerGROUPClientEntity!!
    }

    fun incGroupByContainerTypeClientS(containerGROUPClientTypeEntity: ContainerGROUPClientTypeEntity) {
        var containers = containerGROUPClientTypeEntity.containers
        var min: Double? = containers.minOf {
            it.volume ?: Dnull
        }

        if(min == Dnull) {
            min = null
        }

        val container = containers.find { it.volume == min }!!
        val newVolume = (container.volume ?: 0.0) + 1.0
        database.setContainerGROUPClientTypeVolume(containerGROUPClientTypeEntity, container.containerId!!, newVolume)
        set_PlatformLiveData()
    }

//todo:~r_dos
    private fun set_PlatformLiveData() {
        mPlatformEntity = null
        mPlatformEntity = getPlatformEntity()

        mPlatformMediaEntity = null
        mPlatformMediaEntity = getPlatformMediaEntity()

        mPlatformVoiceCommentEntity = null
        getPlatformVoiceCommentEntity()

        _PlatformLiveData.postValue(mPlatformEntity!!)
    }

    fun decGroupByContainerTypeClientS(typeClientEntity: ContainerGROUPClientTypeEntity) {
        val containers = typeClientEntity.containers

        if(containers.all { it.volume == null }) {
            return
        }

        val filteredContainers = containers.filter { it.volume != null }
        val max: Double = filteredContainers.maxOf {
            it.volume!!
        }

        if(max == 0.0) {
            return
        }

        val container = containers.findLast {
            it.volume == max
        }!!
        val newVolume = container.volume!! - 1f
        database.setContainerGROUPClientTypeVolume(typeClientEntity, container.containerId!!, newVolume)
        set_PlatformLiveData()
    }

    // TODO: !!! ))
    fun getGroupByContainerTypeClientS(client: String?): MutableList<ContainerGROUPClientTypeEntity> {
        LOG.debug("before, client = ${client}")
        // TODO: !!!
//        if (mGroupByContainerTypeClientEntity == null) {
            mContainerGROUPClientTypeEntity = database.loadContainerGROUPClientTypeEntityS(this.getPlatformId(), client)
            if (mContainerGROUPClientTypeEntity == null) {
                database.createGroupByContainerEntityS(this.getPlatformId())
                mContainerGROUPClientTypeEntity = database.loadContainerGROUPClientTypeEntityS(this.getPlatformId(), client)
            }
            if (mContainerGROUPClientTypeEntity == null) {
                LOG.error("mGroupByContainerClientTypeEntity == null")
                mContainerGROUPClientTypeEntity = mutableListOf(ContainerGROUPClientTypeEntity.createEmpty())
            }
//        }
        LOG.debug("mGroupByContainerClientTypeEntity = ${mContainerGROUPClientTypeEntity!!.size}")
        return mContainerGROUPClientTypeEntity!!
    }


    fun getFailReasonS(): List<String> {
        var result = _failReasonS.value!!
        if (result.isEmpty()) {
            result = database.findAllFailReason()
        }
        _failReasonS.postValue(result)
        return result
    }



    fun updateContainerVolume(containerId: Int, volume: Double?) {
        database.updateContainerVolume(this.getPlatformId(), containerId, volume)
        set_PlatformLiveData()
//        getContainerEntity(containerId)
//        getPlatformEntity(platformId)
    }

    fun updatePlatformComment(comment: String) {
        database.updatePlatformComment(this.getPlatformId(), comment)
    }

    fun updateContainerComment(containerId: Int, comment: String?) {
        database.updateContainerComment(this.getPlatformId(), containerId, comment)
//        set_PlatformLiveData()
    }

    fun updatePlatformStatusUnfinished() {
        database.updatePlatformStatusUnfinished(getPlatformId())
//        getPlatformEntity(mPlatformEntityLiveData.value!!.platformId!!)
    }

    fun updateVolumePickup(volume: Double?) {
        database.updateVolumePickup(this.getPlatformId(), volume)
        set_PlatformLiveData()
    }

    fun updatePlatformKGO(kgoVolume: String, isServedKGO: Boolean) {
        database.updatePlatformKGO(this.getPlatformId(), kgoVolume, isServedKGO)
        set_PlatformLiveData()
    }

    fun updatePlatformStatusSuccess() {
        database.updatePlatformStatusSuccess(this.getPlatformId())
    }



    fun buildMapNavigator(
        point: Point,
        checkPoint: Point, drivingRouter: DrivingRouter,
        drivingSession: DrivingSession.DrivingRouteListener
    ) {
        val drivingOptions = DrivingOptions()
        drivingOptions.routesCount = 1
        drivingOptions.avoidTolls = true
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                point,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                checkPoint,
                RequestPointType.WAYPOINT,
                null
            )
        )
        drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, drivingSession)
    }

    fun updateContainerFailure(containerId: Int, failText: String, commentText: String) {
        database.setStateFailureForContainer(this.getPlatformId(), containerId, failText, commentText)
        set_PlatformLiveData()
    }

    fun updateContainerBreakDown(containerId: Int, failText: String, commentText: String) {
        database.setStateBreakdownForContainer(this.getPlatformId(), containerId, failText, commentText)
        set_PlatformLiveData()
    }

    fun addBeforeMedia(imageS: List<ImageEntity>) {
        database.addBeforeMedia(this.getPlatformId(), imageS)
        set_PlatformLiveData()
    }

    fun addBeforeMediaUnload(imageS: List<ImageEntity>) {
        database.addBeforeMediaUnload(this.getPlatformId(), imageS)
        set_PlatformLiveData()
    }

    fun addAfterMediaUnload(imageS: List<ImageEntity>) {
        database.addAfterMediaUnload(this.getPlatformId(), imageS)
        set_PlatformLiveData()
    }

    fun addAfterMedia(imageS: List<ImageEntity>) {
        LOG.debug("before.imageS=${imageS.size}")
        database.addAfterMedia(this.getPlatformId(), imageS)
        set_PlatformLiveData()
        LOG.debug("after.imageS=${imageS.size}")
    }

    fun addBeforeMediaComntainerByTypes(imageS: List<ImageEntity>) {
        database.addBeforeMediaComntainerByTypes(this.getPlatformId(), imageS)
        set_PlatformLiveData()
    }

    fun addFailureMediaContainer(containerId: Int, imageS: List<ImageEntity>) {
        database.addFailureMediaContainer(this.getPlatformId(), containerId, imageS)
        set_PlatformLiveData()
    }

    fun addBreakdownMediaContainer(containerId: Int, imageS: List<ImageEntity>) {
        database.addBreakdownMediaContainer(this.getPlatformId(), containerId, imageS)
        set_PlatformLiveData()
    }


    fun addVoiceComment(platformVoiceCommentEntity: PlatformVoiceCommentEntity) {
        if (platformVoiceCommentEntity.voiceByteArray == null) {
            LOG.error("platformVoiceCommentEntity.voiceByteArray == null")
            return
        }
        database.addVoiceComment(platformVoiceCommentEntity)
        set_PlatformLiveData()
    }

    fun removeVoiceComment(platformVoiceCommentEntity: PlatformVoiceCommentEntity) {
        if (platformVoiceCommentEntity.voiceByteArray == null) {
            LOG.error("platformVoiceCommentEntity.voiceByteArray == null")
            return
        }
        database.removeVoiceComment(platformVoiceCommentEntity)
        set_PlatformLiveData()
    }

    fun isUnloadMode(): Boolean {
        return database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
    }
}