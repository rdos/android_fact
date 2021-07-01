package ru.smartro.worknote.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
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
import ru.smartro.worknote.ui.log.LogActivity
import ru.smartro.worknote.ui.platform_service.PlatformServiceActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.ClusterIcon
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum
import ru.smartro.worknote.work.SynchronizeWorker
import java.util.concurrent.TimeUnit
import kotlin.math.round


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener,
    UserLocationObjectListener, MapObjectTapListener,
    PlatformAdapter.PlatformClickListener, LocationListener {
    var drivingModeState = false

    private val REQUEST_EXIT = 41
    private var firstTime = true
    private val viewModel: MapViewModel by viewModel()
    private val clusterListener = this as ClusterListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        initSynchronizeWorker()
        initUserLocation()
        initMapView()
        initBottomBehavior()
        initDriveMode()
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
                routes.forEach {
                    mapObjects.addPolyline(it.geometry)
                }
            }

        }
        navigator_toggle_fab.setOnClickListener {
            warningClearNavigator("Отменить текущий маршрут?").let {
                it.accept_btn.setOnClickListener {
                    drivingModeState = false
                    navigator_toggle_fab.isVisible = drivingModeState
                    mapObjects.clear()
                    hideDialog()
                }
            }
        }

        log_fab.setOnClickListener {
            startActivity(Intent(this@MapActivity, LogActivity::class.java))
        }

    }

    private fun initSynchronizeWorker() {
        AppPreferences.workerStatus = true
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
    }

    @SuppressLint("MissingPermission")
    private fun initUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        locationManager = mapKit.createLocationManager()
        locationManager.subscribeForLocationUpdates(0.0, 1000, 1.0, false, FilteringMode.OFF, locationListener)

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

    private fun initMapView() {
        fun createPoints(list: RealmList<PlatformEntity>, status: String): List<Point> {
            return list.filter { it.status == status }.map {
                Point(it.coords[0]!!, it.coords[1]!!)
            }
        }

        viewModel.findWayTask().let {
            val clusterCollection: ClusterizedPlacemarkCollection = map_view.map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)
            val greenIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_green_marker)
            val blueIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_blue_marker)
            val redIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_red_marker)
            val orangeIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_orange_marker)
            clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.SUCCESS), greenIcon, IconStyle())
            clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.NEW), blueIcon, IconStyle())
            clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.ERROR), redIcon, IconStyle())
            clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.UNFINISHED), orangeIcon, IconStyle())
            clusterCollection.addTapListener(mapObjectTapListener)
            clusterCollection.clusterPlacemarks(60.0, 15)
        }
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
        locationManager.unsubscribe(locationListener)
        MapKitFactory.getInstance().onStop()
    }

    override fun onClusterAdded(cluster: Cluster) {
        cluster.appearance.setIcon(ClusterIcon(cluster.size.toString(), this))
        cluster.addClusterTapListener(this)
    }

    override fun onClusterTap(cluster: Cluster): Boolean {
        map_view.map.move(
            CameraPosition(cluster.appearance.geometry, 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
        return true
    }

    //UserLocation
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.ic_vehicle_png))
        userLocationView.accuracyCircle.isVisible = false
        userLocationLayer.setObjectListener(null)
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    // Нажатие на маркер (точки)
    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        try {
            val placeMark = mapObject as PlacemarkMapObject
            val coordinate = placeMark.geometry
            val clickedPlatform = viewModel.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
            PlaceMarkDetailDialog(clickedPlatform, coordinate).show(supportFragmentManager, "PlaceMarkDetailDialog")
        } catch (e: Exception) {
            toast("Не удалось загрузить")
        }
        return true
    }

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
                        val currentCoordinate = AppPreferences.currentCoordinate!!
                        if (currentCoordinate.contains("#")) {
                            long = currentCoordinate.substringAfter("#").toDouble()
                            lat = currentCoordinate.substringBefore("#").toDouble()
                        }
                        val synchronizeBody = SynchronizeBody(AppPreferences.wayBillId, listOf(lat, long), deviceId, lastPlatforms)
                        warningAlert("Не все данные отправлены нa сервер").let {
                            it.accept_btn.setOnClickListener {
                                loadingShow()
                                viewModel.sendLastPlatforms(synchronizeBody)
                                    .observe(this, Observer { result ->
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
        showEarlyComplete(allReasons).let { view ->
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
        val intent = Intent(this, PlatformServiceActivity::class.java)
        intent.putExtra("platform_id", item.platformId)
        startActivity(intent)
    }

    override fun startPlatformProblem(item: PlatformEntity) {
        warningCameraShow("Сделайте фото проблемы").let {
            it.accept_btn.setOnClickListener {
                hideDialog()
                val intent = Intent(this, ExtremeProblemActivity::class.java)
                intent.putExtra("platform_id", item.platformId)
                startActivityForResult(intent, REQUEST_EXIT)
            }

            it.dismiss_btn.setOnClickListener {
                hideDialog()
            }
        }
    }

    override fun moveCameraPlatform(point: Point) {
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        map_view.map.move(CameraPosition(point, 16.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
    }

    override fun navigatePlatform(checkPoint: Point) {
        if (drivingModeState) {
            warningClearNavigator("У вас уже есть построенный маршрут. Отменить старый и построить новый?").let {
                it.accept_btn.setOnClickListener {
                    buildNavigator(checkPoint)
                }
            }
        } else {
            warningNavigatePlatform().let {
                it.accept_btn.setOnClickListener {
                    buildNavigator(checkPoint)
                }
            }
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
            toast("Невозможно построить маршрут, повторите попытку.")
        }

    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        initMapView()
        initBottomBehavior()
    }

    private fun moveCameraToUser(location: Location) {
        map_view.map.move(
            CameraPosition(location.position, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
    }

    override fun onLocationStatusUpdated(p0: LocationStatus) {


    }

    override fun onLocationUpdated(p0: Location) {
        currentLocation = p0
        AppPreferences.currentCoordinate = "${p0.position.longitude}#${p0.position.latitude}"
        if (drivingModeState && MyUtil.calculateDistance(p0.position, selectedPlatformToNavigate) <= MIN_METERS) {
            alertOnPoint().let {
                it.dismiss_btn.setOnClickListener {
                    mapObjects.clear()
                }
            }
        }
        if (firstTime) {
            moveCameraToUser(p0)
            firstTime = false
        }
    }
}