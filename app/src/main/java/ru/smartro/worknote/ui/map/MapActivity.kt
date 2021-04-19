package ru.smartro.worknote.ui.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
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
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import kotlinx.android.synthetic.main.behavior_points.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.PlatformAdapter
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.way_task.PlatformEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.ui.choose.way_task_4.WayTaskActivity
import ru.smartro.worknote.ui.platform_service.PlatformServiceActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.ClusterIcon
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum
import ru.smartro.worknote.work.SynchronizeWorker
import java.util.concurrent.TimeUnit


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener, UserLocationObjectListener, MapObjectTapListener, PlatformAdapter.ContainerClickListener {
    private val REQUEST_EXIT = 41
    private val POINT_SERVICE_CODE = 10
    private val TAG = "MapActivity_LOG"
    private var firstTime = true
    private val viewModel: MapViewModel by viewModel()
    private lateinit var wayTaskEntity: WayTaskEntity
    private lateinit var userLocationLayer: UserLocationLayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        wayTaskEntity = viewModel.findWayTask()
        initUploadDataWorker()
        initUserLocation()
        initMapView()
        initBottomBehavior()
    }

    private fun initUploadDataWorker() {
        val uploadDataWorkManager
                = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
    }

    private fun initUserLocation() {
        var locationM = com.yandex.mapkit.location.Location()
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapKit.createLocationManager()
        mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
                override fun onLocationStatusUpdated(p0: LocationStatus) {

                }

                override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
                    locationM = p0
                    AppPreferences.currentCoordinate = "${p0.position.longitude}#${p0.position.latitude}"
                    toast("Клиент найден")
                    if (firstTime) {
                        map_view.map.move(CameraPosition(p0.position, 12.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
                        firstTime = false
                    }
                }
            })
        location_fab.setOnClickListener {
            try {
                map_view.map.move(CameraPosition(locationM.position, 12.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

    }

    private fun initMapView() {
        val wayInfo = viewModel.findWayTask()
        val dsa = viewModel.findWayTaskLV()
        Log.d(TAG, "initMapView: ${dsa}")
        val clusterCollection: ClusterizedPlacemarkCollection = map_view.map.mapObjects.addClusterizedPlacemarkCollection(this)
        val greenIcon = ImageProvider.fromResource(this, R.drawable.ic_green_marker)
        val blueIcon = ImageProvider.fromResource(this, R.drawable.ic_blue_marker)
        val redIcon = ImageProvider.fromResource(this, R.drawable.ic_red_marker)
        clusterCollection.addPlacemarks(createPoints(wayInfo.platfroms!!, StatusEnum.COMPLETED), greenIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.platfroms!!, StatusEnum.EMPTY), blueIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.platfroms!!, StatusEnum.BREAKDOWN), redIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.platfroms!!, StatusEnum.FAILURE), redIcon, IconStyle())
        clusterCollection.addTapListener(this)
        clusterCollection.clusterPlacemarks(60.0, 15)
    }

    private fun createPoints(list: RealmList<PlatformEntity>, status: Int): List<Point> {
        return list.filter { it.status == status }.map {
            Point(it.lat!!, it.lon!!)
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
        map_view.map.move(CameraPosition(cluster.appearance.geometry, 14.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
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
            val wayInfo = viewModel.findWayTask()
            val clickedPlatform = wayInfo.platfroms!!.find { it.lat!! == coordinate.latitude && it.lon!! == coordinate.longitude }!!
            PlaceMarkDetailDialog(clickedPlatform).show(supportFragmentManager, "PlaceMarkDetailDialog")
        } catch (e: Exception) {
            toast("Не удалось загрузить")
        }
        return true
    }

    private fun initBottomBehavior() {
        val wayInfo = viewModel.findWayTask()
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        val platformsArray = wayInfo.platfroms!!

        platformsArray.sortByDescending { it.status == StatusEnum.EMPTY }

        map_behavior_rv.adapter = PlatformAdapter(this, platformsArray)

        map_behavior_header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val hasNotServedPlatform = wayInfo.platfroms!!.any { it.status == StatusEnum.EMPTY }
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

    private fun finishWay(boolean: Boolean) {
        if (!boolean) {
            completeWayBill()
        } else {
            val allReasons = viewModel.findCancelWayReason()
            showFailureFinishWay(allReasons).run {
                this.accept_btn.setOnClickListener {
                    if (!this.reason_et.text.isNullOrEmpty()) {
                        val failureId = allReasons.find { it.problem == this.reason_et.text.toString() }!!.id
                        val body = EarlyCompleteBody(datetime = MyUtil.timeStamp(), failureId = failureId)
                        loadingShow()
                        viewModel.earlyComplete(AppPreferences.wayTaskId, body)
                            .observe(this@MapActivity, Observer { result ->
                                when (result.status) {
                                    Status.SUCCESS -> {
                                        completeWayBill()
                                        loadingHide()
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
                        toast("Выберите причину")
                    }
                }
            }

        }

    }

    private fun completeWayBill() {
        showCompleteEnterInfo().run {
            this.accept_btn.setOnClickListener {
                if (this.weight_tg.isChecked || this.volume_tg.isChecked) {
                    val unloadType = if (this.volume_tg.isChecked) 1 else 2
                    val body = CompleteWayBody(finishedAt = MyUtil.timeStamp(), unloadType = unloadType, unloadValue = "${this.comment_et.text.toString()}.00")
                    loadingShow()
                    viewModel.completeWay(AppPreferences.wayTaskId, body)
                        .observe(this@MapActivity, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    AppPreferences.thisUserHasTask = false
                                    viewModel.clearData()
                                    loadingHide()
                                    showSuccessComplete().run {
                                        this.accept_btn.setOnClickListener {
                                            startActivity(Intent(this@MapActivity, WayTaskActivity::class.java))
                                            finish()
                                        }
                                        this.exit_btn.setOnClickListener {
                                            MyUtil.logout(this@MapActivity)
                                        }
                                    }
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
        val itemJson = Gson().toJson(item)
        intent.putExtra("container", itemJson)
        startActivityForResult(intent, POINT_SERVICE_CODE)
    }

    override fun startPlatformProblem(item: PlatformEntity) {
        val intent = Intent(this, ExtremeProblemActivity::class.java)
        intent.putExtra("wayPoint", Gson().toJson(item))
        intent.putExtra("isContainerProblem", false)
        startActivityForResult(intent, REQUEST_EXIT)
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
}