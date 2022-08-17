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
import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LoG
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.GroupByContainerClientEntity
import ru.smartro.worknote.work.GroupByContainerClientTypeEntity
import ru.smartro.worknote.work.PlatformEntity

class ServePlatformVM(app: Application) : AViewModel(app) {

    private var mGroupByContainerClientEntity: MutableList<GroupByContainerClientEntity>? = null
    private var mGroupByContainerClientTypeEntity: MutableList<GroupByContainerClientTypeEntity>? = null
    private var mPlatformEntity: PlatformEntity? = null
    private var mPlatformId: Int = Inull
    private val mPlatformMutableLiveData: MutableLiveData<PlatformEntity> = MutableLiveData(PlatformEntity())
    val mPlatformEntityLiveData: LiveData<PlatformEntity>
        get() = mPlatformMutableLiveData

    val mContainerEntityLiveData: LiveData<PlatformEntity>
        get() = mPlatformMutableLiveData

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val mFailReasonS: LiveData<List<String>>
        get() = _failReasonS

    fun getPlatformEntity(): PlatformEntity {
//        if (mPlatformEntity == null) {
            mPlatformEntity = database.getPlatformEntity(mPlatformId)
//            LoG.trace("result=${mPlatformEntity}")
//        }
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


    fun setPlatformEntity(platformEntity: PlatformEntity){
//        mPlatformEntity = platformEntity
        if (mPlatformId == platformEntity.platformId) {
            return
        }
        mPlatformId = platformEntity.platformId
        mPlatformEntity = null
        mGroupByContainerClientEntity = null
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


    fun getGroupByContainerClientS(): MutableList<GroupByContainerClientEntity> {
        mGroupByContainerClientEntity = database.loadGroupByContainerClient(mPlatformId)
        return mGroupByContainerClientEntity!!
    }


    fun getGroupByContainerClientTypeS(client: String?): MutableList<GroupByContainerClientTypeEntity> {
        mGroupByContainerClientTypeEntity = database.loadGroupByContainerClientTypeEntity(mPlatformId, client)
        return mGroupByContainerClientTypeEntity!!
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
//        getContainerEntity(containerId)
//        getPlatformEntity(platformId)

    }

    fun updateContainerComment(containerId: Int, comment: String?) {
        database.updateContainerComment(this.getPlatformId(), containerId, comment)

    }

    fun updatePlatformStatusUnfinished() {
        database.updatePlatformStatusUnfinished(getPlatformId())
//        getPlatformEntity(mPlatformEntityLiveData.value!!.platformId!!)
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        database.updateVolumePickup(platformId, volume)
//        getPlatformEntity(platformId)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        database.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
//        getPlatformEntity(platformId)
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