package ru.smartro.worknote.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import kotlinx.android.synthetic.main.behavior_platforms.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.PlatformAdapter
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.ui.debug.DebugActivity
import ru.smartro.worknote.ui.journal.JournalAct
import ru.smartro.worknote.ui.platform_serve.PlatformServeActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum
import ru.smartro.worknote.work.SynchronizeWorker
import java.util.concurrent.TimeUnit
import kotlin.math.round

import ru.smartro.worknote.base.AbstractAct


class MapActivity : AbstractAct(),
    /*UserLocationObjectListener,*/ MapObjectTapListener,
    PlatformAdapter.PlatformClickListener, LocationListener {
    var drivingModeState = false

    private val REQUEST_EXIT = 41
    private var firstTime = true
    private var isOnPointFirstTime = true
    private val viewModel: MapViewModel by viewModel()

    private val mapObjectTapListener = this as MapObjectTapListener
    private val platformClickListener = this as PlatformAdapter.PlatformClickListener

    private val locationListener = this as LocationListener
    private val MIN_METERS = 50

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var drivingSession: DrivingSession.DrivingRouteListener
    private lateinit var currentLocation: Location
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var selectedPlatformToNavigate: Point
    private lateinit var locationManager: LocationManager
    private lateinit var mapKit: MapKit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        initSynchronizeWorker()
        initMapView(false)
        initBottomBehavior()
        initUserLocation()
        initDriveMode()
        // TODO: 21.10.2021 r_dos 
//        map_toast.setOnClickListener{
//            toast("${AppPreferences.wayBillId}")
//        }
    }

    private fun initDriveMode() {
        selectedPlatformToNavigate = Point(0.0, 0.0)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = map_view.map.mapObjects.addCollection()
        drivingSession = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutesError(p0: Error) {
                toast("Ошибка при построении маршрута")
            }

            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                routes.forEach { mapObjects.addPolyline(it.geometry) }
            }

        }
        navigator_toggle_fab.setOnClickListener {
            drivingModeState = false
            navigator_toggle_fab.isVisible = drivingModeState
            mapObjects.clear()
            hideDialog()
        }

        log_fab.setOnClickListener {
            startActivity(Intent(this@MapActivity, JournalAct::class.java))
        }

    }

    private fun initSynchronizeWorker() {
        Log.w(TAG, "initSynchronizeWorker.before thread_id=${Thread.currentThread().id}")
        AppPreferences.workerStatus = true
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
        Log.d(TAG, "initSynchronizeWorker.after")
    }

    @SuppressLint("MissingPermission")
    private fun initUserLocation() {
        mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
//        userLocationLayer.setObjectListener(this)

        location_fab.setOnClickListener {
            try {
                moveCameraToUser(currentLocation)
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

        debug_fab.setOnClickListener {
            startActivity(Intent(this, DebugActivity::class.java))
        }
    }

    private fun initMapView(isUpdateView: Boolean) {
        viewModel.findWayTask().let {
            val mapObjectCollection = map_view.map.mapObjects
            if (isUpdateView) {
                mapObjectCollection.removeTapListener(mapObjectTapListener)
            }
            addPlaceMarks(this, mapObjectCollection, it.platforms)
            mapObjectCollection.addTapListener(mapObjectTapListener)
        }
    }

    private fun getIconViewProvider(_context: Context, drawableResId: Int): ViewProvider {
        fun iconMarker(_drawableResId: Int): View {
            val resultIcon = View(_context).apply { background = ContextCompat.getDrawable(context, _drawableResId) }
            return resultIcon
        }
        return ViewProvider(iconMarker(drawableResId))
    }

    private fun addPlaceMarks(context: Context, mapObjectCollection: MapObjectCollection, platforms: RealmList<PlatformEntity>) {
        platforms.forEach {
            mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
            // TODO: 22.10.2021 wtf???!!!start
//            when (it.status) {
//                StatusEnum.NEW -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.SUCCESS -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.ERROR -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.UNFINISHED -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//            }
            // TODO: 22.10.2021 WTF?!!!stop
        }
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
        MapKitFactory.getInstance().onStart()
    }


//    override fun onObjectAdded(userLocationView: UserLocationView) {
//        val pinIcon = userLocationView.arrow.useCompositeIcon()
//        pinIcon.setIcon(
//            "icon",
//            ImageProvider.fromResource(this, R.drawable.ic_truck_icon),
//            IconStyle().setAnchor(PointF(0.5f, 1f))
//                .setRotationType(RotationType.ROTATE)
//                .setZIndex(0f)
//        )
//
//        userLocationView.accuracyCircle.isVisible = false
//        userLocationLayer.setObjectListener(null)
//    }

//    override fun onObjectRemoved(p0: UserLocationView) {
//
//    }
//
//    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
//
//    }

    private fun initBottomBehavior() {
        viewModel.findWayTask().let {

            bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
            val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
            val platformsArray = it.platforms
            platformsArray.sortBy { it.updateAt }
            map_behavior_rv.adapter = PlatformAdapter(platformClickListener, platformsArray)

            map_behavior_header.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                else
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            val hasNotServedPlatform = it.platforms.any { found -> found.status == StatusEnum.NEW }
            if (hasNotServedPlatform) {
                map_behavior_send_btn.background = getDrawable(R.drawable.bg_button_red)
                map_behavior_send_btn.text = getString(R.string.finish_way_now)
            } else {
                map_behavior_send_btn.background = getDrawable(R.drawable.bg_button)
                map_behavior_send_btn.text = getString(R.string.finish_way)
            }
            map_behavior_send_btn.setOnClickListener {
                val lastPlatforms = viewModel.findLastPlatforms()
                if (lastPlatforms.isEmpty()) {
                    finishWay(hasNotServedPlatform)
                } else {

                    var long = 0.0
                    var lat = 0.0
                    val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                    val currentCoordinate = AppPreferences.currentCoordinate
                    if (currentCoordinate.contains("#")) {
                        long = currentCoordinate.substringAfter("#").toDouble()
                        lat = currentCoordinate.substringBefore("#").toDouble()
                    }
                    val synchronizeBody = SynchronizeBody(AppPreferences.wayBillId, listOf(lat, long), deviceId, lastPlatforms)
                    warningAlert("Не все данные отправлены нa сервер").let {
                        it.accept_btn.setOnClickListener {
                            loadingShow()
                            viewModel.sendLastPlatforms(synchronizeBody).observe(this, { result ->
                                    when (result.status) {
                                        Status.SUCCESS -> {
                                            loadingHide()
                                            hideDialog()
                                            finishWay(hasNotServedPlatform)
                                        }
                                        Status.ERROR -> {
                                            toast(result.msg)
                                            loadingHide()
                                        }
                                        Status.NETWORK -> {
                                            toast("Проблемы с интернетом")
                                            loadingHide()
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        }
    }

    private fun finishWay(boolean: Boolean) {
        if (!boolean) {
            successCompleteWayBill()
        } else {
            earlyCompleteWayBill()
        }
    }

    private fun earlyCompleteWayBill() {
        val allReasons = viewModel.findCancelWayReason()
        showDialogEarlyComplete(allReasons).let { view ->
            val totalVolume = viewModel.findContainersVolume()
            view.unload_value_et.setText("$totalVolume")
            view.accept_btn.setOnClickListener {
                if (!view.reason_et.text.isNullOrEmpty() &&
                    (view.early_volume_tg.isChecked || view.early_weight_tg.isChecked)
                    && !view.unload_value_et.text.isNullOrEmpty()
                ) {
                    val failureId = viewModel.findCancelWayReasonByValue(view.reason_et.text.toString())
                    val unloadValue = round(
                        view.unload_value_et.text.toString().toDouble() * 100
                    ) / 100
                    val unloadType = if (view.early_volume_tg.isChecked) 1 else 2
                    val body = EarlyCompleteBody(failureId, MyUtil.timeStamp(), unloadType, unloadValue)
                    loadingShow()

                    viewModel.earlyComplete(AppPreferences.wayTaskId, body)
                        .observe(this@MapActivity, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    viewModel.finishTask(this)
                                }
                                Status.ERROR -> {
                                    toast(result.msg)
                                    loadingHide()
                                }
                                Status.NETWORK -> {
                                    toast("Проблемы с интернетом")
                                    loadingHide()
                                }
                            }
                        })
                } else {
                    toast("Заполните все поля")
                }
            }
            view.dismiss_btn.setOnClickListener {
                hideDialog()
            }
        }
    }

    private fun successCompleteWayBill() {
        val totalVolume = viewModel.findContainersVolume()
        showCompleteWaybill().run {
            this.comment_et.setText("$totalVolume")
            this.accept_btn.setOnClickListener {
                if (this.weight_tg.isChecked || this.volume_tg.isChecked) {
                    val unloadType = if (this.volume_tg.isChecked) 1 else 2
                    val unloadValue = round(this.comment_et.text.toString().toDouble() * 100) / 100
                    val body = CompleteWayBody(
                        finishedAt = MyUtil.timeStamp(),
                        unloadType = unloadType, unloadValue = unloadValue.toString()
                    )
                    loadingShow()
                    viewModel.completeWay(AppPreferences.wayTaskId, body)
                        .observe(this@MapActivity, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    viewModel.finishTask(this@MapActivity)
                                }
                                Status.ERROR -> {
                                    toast(result.msg)
                                    loadingHide()
                                }
                                Status.NETWORK -> {
                                    toast("Проблемы с интернетом")
                                    loadingHide()
                                }
                            }
                        })
                } else {
                    toast("Выберите тип показателей")
                }
            }
        }
    }

    override fun startPlatformService(item: PlatformEntity) {
        val intent = Intent(this, PlatformServeActivity::class.java)
        intent.putExtra("platform_id", item.platformId)
        startActivity(intent)
    }

    override fun startPlatformProblem(item: PlatformEntity) {
        hideDialog()
        val intent = Intent(this, ExtremeProblemActivity::class.java)
        intent.putExtra("platform_id", item.platformId)
        startActivityForResult(intent, REQUEST_EXIT)
    }

    override fun moveCameraPlatform(point: Point) {
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        map_view.map.move(CameraPosition(point, 16.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
    }

    override fun navigatePlatform(checkPoint: Point) {
        if (drivingModeState) {
            warningClearNavigator(getString(R.string.way_is_exist)).let {
                it.accept_btn.setOnClickListener {
                    buildNavigator(checkPoint)
                }
            }
        } else {
            buildNavigator(checkPoint)
        }
    }

    fun buildNavigator(checkPoint: Point) {
        try {
            mapObjects.clear()
            selectedPlatformToNavigate = checkPoint
            viewModel.buildMapNavigator(currentLocation, checkPoint, drivingRouter, drivingSession)
            drivingModeState = true
            navigator_toggle_fab.isVisible = drivingModeState
            hideDialog()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            moveCameraToUser(currentLocation)
        } catch (e: Exception) {
            toast(getString(R.string.error_build_way))
        }

    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        initMapView(true)
        initBottomBehavior()
        locationManager = mapKit.createLocationManager()
        locationManager.subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.ON, locationListener)
    }

    private fun moveCameraToUser(location: Location) {
        map_view.map.move(
            CameraPosition(location.position, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
    }

    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
        when (locationStatus) {
            LocationStatus.NOT_AVAILABLE -> Log.d("LogDistance", "GPS STOP")
            LocationStatus.AVAILABLE -> Log.d("LogDistance", "GPS START")

        }
    }

    override fun onLocationUpdated(location: Location) {
//        Log.d("LogDistance", "###################")
        currentLocation = location
        AppPreferences.currentCoordinate = "${location.position.longitude}#${location.position.latitude}"
        val distanceToPoint = MyUtil.calculateDistance(location.position, selectedPlatformToNavigate)
//        Log.d("LogDistance", "Distance: $distanceToPoint")
        if (drivingModeState && distanceToPoint <= MIN_METERS && isOnPointFirstTime) {
            isOnPointFirstTime = false
            alertOnPoint().let {
                it.dismiss_btn.setOnClickListener {
                    drivingModeState = false
                    isOnPointFirstTime = true
                    mapObjects.clear()
                    hideDialog()
                }
            }
        } else {
//            Log.d("LogDistance", "Distance not arrive")
        }
        if (firstTime) {
            moveCameraToUser(location)
            firstTime = false
        }
//        Log.d("LogDistance", "Location updated")
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unsubscribe(locationListener)
        map_view.onStop()
        MapKitFactory.getInstance().onStop()
    }


    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeMark = mapObject as PlacemarkMapObject
        val coordinate = placeMark.geometry
        val clickedPlatform = viewModel.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
        val platformClickedDtlDialog = PlatformClickedDtlDialog(clickedPlatform, coordinate)
        platformClickedDtlDialog.show(supportFragmentManager, "PlaceMarkDetailDialog")
        return true
    }

}