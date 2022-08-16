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
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.work.PlatformEntity

class VMPlatformServeShared(app: Application) : AViewModel(app) {
    

    private val _platformEntity: MutableLiveData<PlatformEntity> = MutableLiveData(null)
    val mPlatformEntity: LiveData<PlatformEntity>
        get() = _platformEntity

    private val _failReasonS: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val mFailReasonS: LiveData<List<String>>
        get() = _failReasonS

    fun getPlatformEntity(platformId: Int): PlatformEntity {
        val response: PlatformEntity = baseDat.getPlatformEntity(platformId)
        _platformEntity.postValue(response)
        return response
    }

    fun getFailReasonS(): List<String> {
        var result = _failReasonS.value!!
        if (result.isEmpty()) {
            result = baseDat.findAllFailReason()
        }
        _failReasonS.postValue(result)
        return result
    }

    fun updateVolumePickup(platformId: Int, volume: Double?) {
        baseDat.updateVolumePickup(platformId, volume)
        getPlatformEntity(platformId)
    }

    fun updatePlatformKGO(platformId: Int, kgoVolume: String, isServedKGO: Boolean) {
        baseDat.updatePlatformKGO(platformId, kgoVolume, isServedKGO)
        getPlatformEntity(platformId)
    }

    fun updatePlatformStatusSuccess(platformId: Int) {
        baseDat.updatePlatformStatusSuccess(platformId)
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