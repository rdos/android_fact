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
import ru.smartro.worknote.App
import ru.smartro.worknote.Dnull
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LoG
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.GroupByContainerClientEntity
import ru.smartro.worknote.work.GroupByContainerTypeClientEntity
import ru.smartro.worknote.work.PlatformEntity

class ServePlatformVM(app: Application) : AViewModel(app) {
    private var mPlatformId: Int = Inull



    private var mPlatformEntity: PlatformEntity? = null

    private val _PlatformLiveData: MutableLiveData<PlatformEntity> = MutableLiveData(PlatformEntity())
    val todoLiveData: LiveData<PlatformEntity>
        get() = _PlatformLiveData
    


    private var mGroupByContainerClientEntity: MutableList<GroupByContainerClientEntity>? = null
    private var mGroupByContainerTypeClientEntity: MutableList<GroupByContainerTypeClientEntity>? = null

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
//    val mFailReasonS: LiveData<List<String>>
//        get() = _failReasonS

    fun getPlatformEntity(): PlatformEntity {
        if (mPlatformEntity == null) {
            mPlatformEntity = database.getPlatformEntity(mPlatformId)
            LoG.trace("result=${mPlatformEntity?.platformId}")
        }
        return mPlatformEntity!!
    }

    fun getPlatformId(): Int {
        val result = getPlatformEntity().platformId
        if (result != mPlatformId) {
            if (App.getAppliCation().isDevelMode()) {
                throw Exception()
            } else {
                LoG.debug(" if (result != mPlatformId)")
                LoG.error(" if (result != mPlatformId)")
                LoG.warn(" if (result != mPlatformId)")
                LoG.info(" if (result != mPlatformId)")
            }
        }
        return mPlatformId
    }


    // TODO: !!!
    fun setPlatformEntity(platformEntity: PlatformEntity) {
        LoG.debug("before.mPlatformId=${mPlatformId}")
        LoG.debug("before.platformEntity.platformId=${platformEntity.platformId}")

//        mPlatformEntity = platformEntity
        if (mPlatformId == platformEntity.platformId) {
            LoG.warn("mPlatformId == platformEntity.platformId")
            return
        }
        mPlatformId = platformEntity.platformId
        mPlatformEntity = null
        mGroupByContainerClientEntity = null

        LoG.trace("after.mPlatformId=${mPlatformId}")
    }

    fun getContainerS(): List<ContainerEntity> {
        var result =  this.getPlatformEntity().containers.toList()

        result = result.sortedBy {
            it.isActiveToday == false
        }
        LoG.trace("result=${result.count()}")
        return result
    }

    fun getContainer(containerId: Int): ContainerEntity {
        LoG.trace("containerId=${containerId}")
        var result =  this.getPlatformEntity().containers.find { it.containerId == containerId}
        if (result == null) {
           result = ContainerEntity.createEmpty()
        }
        LoG.trace("result.isFailureNotEmpty() = ${result.isFailureNotEmpty()}")
        LoG.debug("result = ${result.containerId}")
        return result
    }

    // TODO: !??
    fun getGroupByContainerClientS(): MutableList<GroupByContainerClientEntity> {
        if (mGroupByContainerClientEntity == null) {
            mGroupByContainerClientEntity = database.loadGroupByContainerClient(mPlatformId)
            LoG.info("mGroupByContainerClientEntity START = ${mGroupByContainerClientEntity}")
            if (mGroupByContainerClientEntity == null) {
                database.createGroupByContainerEntityS(mPlatformId)
                mGroupByContainerClientEntity = database.loadGroupByContainerClient(mPlatformId)
                LoG.info("mGroupByContainerClientEntity NULL 1 = ${mGroupByContainerClientEntity}")
            }
            if (mGroupByContainerClientEntity == null) {
                LoG.error("mGroupByContainerClientEntity == null")
                mGroupByContainerClientEntity = mutableListOf(GroupByContainerClientEntity.createEmpty())
            }
        }
        return mGroupByContainerClientEntity!!
    }

    fun incGroupByContainerTypeClientS(typeClientEntity: GroupByContainerTypeClientEntity) {
        var containers = typeClientEntity.containers
        var min: Double? = containers.minOf {
            it.volume ?: Dnull
        }

        if(min == Dnull) {
            val filteredContainers = containers.filter {
                it.volume != null
            }
            min = filteredContainers.minOf {
                it.volume!!
            }
            if(min == 0.0) {
                return
            }
            min = null
        }

        val container = containers.find { it.volume == min }!!
        val newVolume = (container.volume ?: 0.0) + 1.0
        database.setGroByContainerTypeClientVolume(typeClientEntity, container.containerId!!, newVolume)
        set_PlatformLiveData()
    }

    private fun set_PlatformLiveData() {
        mPlatformEntity = getPlatformEntity()
        _PlatformLiveData.postValue(mPlatformEntity!!)
    }

    fun decGroupByContainerTypeClientS(typeClientEntity: GroupByContainerTypeClientEntity) {
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
        val newVolume = container.volume!! - 1.0
        database.setGroByContainerTypeClientVolume(typeClientEntity, container.containerId!!, newVolume)
        set_PlatformLiveData()
    }

    // TODO: !!! ))
    fun getGroupByContainerTypeClientS(client: String?): MutableList<GroupByContainerTypeClientEntity> {
        LoG.debug("before, client = ${client}")
        mGroupByContainerTypeClientEntity = database.loadGroupByContainerTypeClientEntity(mPlatformId, client)
        if (mGroupByContainerTypeClientEntity == null) {
            database.createGroupByContainerEntityS(mPlatformId)
            mGroupByContainerTypeClientEntity = database.loadGroupByContainerTypeClientEntity(mPlatformId, client)
        }
        if (mGroupByContainerTypeClientEntity == null) {
            LoG.error("mGroupByContainerClientTypeEntity == null")
            mGroupByContainerTypeClientEntity = mutableListOf(GroupByContainerTypeClientEntity.createEmpty())
        }
        LoG.debug("mGroupByContainerClientTypeEntity = ${mGroupByContainerTypeClientEntity!!.size}")
        return mGroupByContainerTypeClientEntity!!
    }


    fun getFailReasonS(): List<String> {
        var result = _failReasonS.value!!
        if (result.isEmpty()) {
            result = database.findAllFailReason()
        }
        _failReasonS.postValue(result)
        return result
    }




    fun updateContainerVolume( containerId: Int, volume: Double?) {
        database.updateContainerVolume(this.getPlatformId(), containerId, volume)
        set_PlatformLiveData()
//        getContainerEntity(containerId)
//        getPlatformEntity(platformId)
    }

    fun updateContainerComment(containerId: Int, comment: String?) {
        database.updateContainerComment(this.getPlatformId(), containerId, comment)
//        set_PlatformLiveData()
    }

    fun updatePlatformStatusUnfinished() {
        database.updatePlatformStatusUnfinished(getPlatformId())
//        getPlatformEntity(mPlatformEntityLiveData.value!!.platformId!!)
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        database.updateVolumePickup(platformId, volume)
        set_PlatformLiveData()
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        database.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
        set_PlatformLiveData()
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        database.updatePlatformStatusSuccess(platformId)

//        fun updatePlatformStatusSuccess(platformId: Int) {
////        mServedContainers.value?.let {
////            if (_sortedContainers.value != null ) {
////                it.forEach { el ->
////                    val containers = _sortedContainers.value!!.find { sorted -> sorted.client == el.client }!!.typeGroupedContainers.find { typed -> typed.typeName == el.typeName }!!.containersIds
////                    val newVolume = el.servedCount.toDouble() / containers.size
////                    containers.forEach { cont ->
////                        baseDat.updateContainerVolume(platformId, cont, newVolume)
////                    }
////                }
////            }
////
////        }
////        baseDat.updatePlatformStatusSuccess(platformId)
//        }
    }






    fun onDecrease(clientName: String, typeName: String) {
//        val servedContainers = mServedContainers.value!!.toMutableList()
//        val servedCluster = servedContainers.find { el -> el.client == clientName && el.typeName == typeName }
//        val newCount = servedCluster!!.servedCount - 1
//
//        if(newCount >= 0) {
//            val clusterIndex = servedContainers.indexOf(servedCluster)
//            servedContainers[clusterIndex].servedCount = newCount
//
//            baseDat.updatePlatformServedContainers(_platformEntity.value!!.platformId!!, servedContainers)
//            mServedContainers.postValue(servedContainers)
//        }

    }

    fun onIncrease(clientName: String, typeName: String) {
//        val servedContainers = mServedContainers.value!!.toMutableList()
//        val servedCluster = servedContainers.find { el -> el.client == clientName && el.typeName == typeName }
//        val newCount = servedCluster!!.servedCount + 1
//
//        val clusterIndex = servedContainers.indexOf(servedCluster)
//        servedContainers[clusterIndex].servedCount = newCount
//
//        baseDat.updatePlatformServedContainers(_platformEntity.value!!.platformId!!, servedContainers)
//        mServedContainers.postValue(servedContainers)
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
}