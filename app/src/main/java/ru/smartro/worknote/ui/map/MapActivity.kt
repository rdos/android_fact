package ru.smartro.worknote.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import kotlinx.android.synthetic.main.behavior_points.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.PlatformAdapter
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.ui.debug.DebugActivity
import ru.smartro.worknote.ui.platform_service.PlatformServiceActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.ClusterIcon
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum
import ru.smartro.worknote.work.SynchronizeWorker
import java.util.concurrent.TimeUnit


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener,
    UserLocationObjectListener, MapObjectTapListener,
    PlatformAdapter.PlatformClickListener, android.location.LocationListener {

    private val REQUEST_EXIT = 41
    private var firstTime = true
    private val viewModel: MapViewModel by viewModel()
    private lateinit var userLocationLayer: UserLocationLayer
    private val locationListener = this as android.location.LocationListener
    private val clusterListener = this as ClusterListener
    private val mapObjectTapListener = this as MapObjectTapListener
    private val platformClickListener = this as PlatformAdapter.PlatformClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        initUploadDataWorker()
        initUserLocation()
        initMapView()
        initBottomBehavior()
    }

    private fun initUploadDataWorker() {
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
        AppPreferences.workerStatus = true
    }

    @SuppressLint("MissingPermission")
    private fun initUserLocation() {
        var locationM = Location()
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 10f, locationListener)

        mapKit.createLocationManager()
        mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {

            }

            override fun onLocationUpdated(p0: Location) {
                locationM = p0
                AppPreferences.currentCoordinate = "${p0.position.longitude}#${p0.position.latitude}"
                toast("Клиент найден")
                if (firstTime) {
                    map_view.map.move(
                        CameraPosition(p0.position, 12.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1F), null
                    )
                    firstTime = false
                }
            }
        })


        location_fab.setOnClickListener {
            try {
                map_view.map.move(
                    CameraPosition(locationM.position, 12.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1F), null
                )
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

        debug_fab.setOnClickListener {
            startActivity(Intent(this, DebugActivity::class.java))
        }
    }

    private fun initMapView() {
        lifecycleScope.launchWhenCreated {
            viewModel.findWayTask().let {
                val clusterCollection: ClusterizedPlacemarkCollection = map_view.map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)
                val greenIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_green_marker)
                val blueIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_blue_marker)
                val redIcon = ImageProvider.fromResource(this@MapActivity, R.drawable.ic_red_marker)
                clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.SUCCESS), greenIcon, IconStyle())
                clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.NEW), blueIcon, IconStyle())
                clusterCollection.addPlacemarks(createPoints(it.platforms, StatusEnum.ERROR), redIcon, IconStyle())
                clusterCollection.addTapListener(mapObjectTapListener)
                clusterCollection.clusterPlacemarks(60.0, 15)
            }
        }
    }

    private fun createPoints(list: RealmList<PlatformEntity>, status: String): List<Point> {
        return list.filter { it.status == status }.map {
            Point(it.coords[0]!!, it.coords[1]!!)
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
        //TODO("Not yet implemented")
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        //TODO("Not yet implemented")
    }

    // Нажатие на маркер (точки)
    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        try {
            val placeMark = mapObject as PlacemarkMapObject
            val coordinate = placeMark.geometry
            val clickedPlatform = viewModel.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
            PlaceMarkDetailDialog(clickedPlatform).show(supportFragmentManager, "PlaceMarkDetailDialog")
        } catch (e: Exception) {
            toast("Не удалось загрузить")
        }
        return true
    }

    private fun initBottomBehavior() {
        lifecycleScope.launchWhenCreated {
            viewModel.findWayTask().let {
                val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
                val platformsArray = it.platforms

                platformsArray.sortByDescending { it.status == StatusEnum.NEW }

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
                    finishWay(hasNotServedPlatform)
                }
            }
        }
    }

    private fun finishWay(boolean: Boolean) {
        if (!boolean) {
            completeWayBill()
        } else {
            val allReasons = viewModel.findCancelWayReason()
            showEarlyComplete(allReasons).let { view ->
                view.accept_btn.setOnClickListener {
                    if (!view.reason_et.text.isNullOrEmpty() && (view.early_volume_tg.isChecked || view.early_weight_tg.isChecked)
                        && !view.unload_value_et.text.isNullOrEmpty()
                    ) {
                        val failureId = viewModel.findCancelWayReasonByValue(view.reason_et.text.toString())
                        val unloadValue = view.unload_value_et.text.toString().toInt()
                        val unloadType = if (view.early_volume_tg.isChecked) 1 else 2

                        val body = EarlyCompleteBody(failureId, MyUtil.timeStamp(), unloadType, unloadValue)
                        loadingShow()

                        viewModel.earlyComplete(AppPreferences.wayTaskId, body)
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
                        toast("Заполните все поля")
                    }
                }
                view.dismiss_btn.setOnClickListener {
                    hideDialog()
                }
            }

        }

    }

    private fun completeWayBill() {
        showCompleteWaybill().run {
            this.accept_btn.setOnClickListener {
                if (this.weight_tg.isChecked || this.volume_tg.isChecked) {
                    val unloadType = if (this.volume_tg.isChecked) 1 else 2
                    val body = CompleteWayBody(
                        finishedAt = MyUtil.timeStamp(),
                        unloadType = unloadType, unloadValue = "${this.comment_et.text.toString()}.00"
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

    override fun moveCameraPoint(point: Point) {
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        map_view.map.move(CameraPosition(point, 16.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        initMapView()
        initBottomBehavior()
    }

    override fun onLocationChanged(p0: android.location.Location) {
        AppPreferences.currentCoordinate = "${p0.longitude}#${p0.latitude}"
    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

}