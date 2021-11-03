package ru.smartro.worknote.ui.map

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.work.WorkManager
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.showSuccessComplete
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.database.entity.work_order.WayTaskEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.ui.choose.way_list_3.WayBillActivity
import ru.smartro.worknote.util.MyUtil
import java.util.*

class MapViewModel(application: Application) : BaseViewModel(application) {


    fun completeWay(id: Int, completeWayBody: CompleteWayBody): LiveData<Resource<EmptyResponse>> {
        return network.completeWay(id, completeWayBody)
    }

    fun earlyComplete(id: Int, body: EarlyCompleteBody): LiveData<Resource<EmptyResponse>> {
        return network.earlyComplete(id, body)
    }

    fun finishTask(context: AppCompatActivity) {
        Log.i(TAG, "clearData")
        context.loadingHide()
        WorkManager.getInstance(context).cancelUniqueWork("UploadData")
        clearData()
        AppPreferences.isHasTask = false
        context.showSuccessComplete().let {
            it.finish_accept_btn.setOnClickListener {
                context.startActivity(Intent(context, WayBillActivity::class.java))
                context.finish()
            }
            it.exit_btn.setOnClickListener {
                MyUtil.logout(context)
            }
        }
    }

    fun clearData() {
        Log.i(TAG, "clearData")
        db.clearData()
    }

    fun findWayTask(): WayTaskEntity {
        return db.findWayTask()
    }

    fun findLastPlatforms() =
        db.findLastPlatforms()

    fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
        return db.findPlatformByCoordinate(lat, lon)
    }

    fun sendLastPlatforms(body: SynchronizeBody): LiveData<Resource<SynchronizeResponse>> {
        return network.sendLastPlatforms(body)
    }

    fun findCancelWayReason(): List<CancelWayReasonEntity> {
        return db.findCancelWayReason()
    }

    fun findCancelWayReasonByValue(reason: String): Int {
        return db.findCancelWayReasonByValue(reason)
    }

    fun findContainersVolume(): Double =
        db.findContainersVolume()

    fun buildMapNavigator(currentLocation: com.yandex.mapkit.location.Location, checkPoint: Point, drivingRouter: DrivingRouter, drivingSession: DrivingSession.DrivingRouteListener) {
        val drivingOptions = DrivingOptions()
        drivingOptions.routesCount = 1
        drivingOptions.avoidTolls = true
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                currentLocation.position,
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

