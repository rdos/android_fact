package ru.smartro.worknote.ui.map

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
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
import kotlinx.android.synthetic.main.alert_point_detail.view.*
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import kotlinx.android.synthetic.main.behavior_points.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayPointAdapter
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.ui.choose.way_task_4.WayTaskActivity
import ru.smartro.worknote.ui.point_service.PointServiceActivity
import ru.smartro.worknote.ui.problem.ContainerProblemActivity
import ru.smartro.worknote.util.ActivityResult
import ru.smartro.worknote.util.ClusterIcon
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener, UserLocationObjectListener, MapObjectTapListener, WayPointAdapter.ContainerClickListener {
    private val REQUEST_EXIT = 41
    private val POINT_SERVICE_CODE = 10
    private val TAG = "MapActivity_LOG"
    private val viewModel: MapViewModel by viewModel()
    private lateinit var wayTaskEntity: WayTaskEntity
    private lateinit var userLocationLayer: UserLocationLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        wayTaskEntity = viewModel.findWayTask()
        Log.d(TAG, "onCreate: wayTaskEntity ${Gson().toJson(wayTaskEntity)} ")
        initMapView()
        initBottomBehavior()
        initUserLocation()
    }


    private fun initUserLocation() {
        var locationM = com.yandex.mapkit.location.Location()
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapKit.createLocationManager()
            .requestSingleUpdate(object : LocationListener, com.yandex.mapkit.location.LocationListener {
                override fun onLocationChanged(location: Location) {

                }

                override fun onLocationStatusUpdated(p0: LocationStatus) {

                }

                override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
                    locationM = p0
                }
            })
        location_fab.setOnClickListener {
            try {
                map_view.map.move(CameraPosition(locationM.position, 14.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

    }

    private fun initMapView() {
        val wayInfo = viewModel.findWayTask()
        val clusterCollection: ClusterizedPlacemarkCollection = map_view.map.mapObjects.addClusterizedPlacemarkCollection(this)
        val greenIcon = ImageProvider.fromResource(this, R.drawable.ic_green_marker)
        val blueIcon = ImageProvider.fromResource(this, R.drawable.ic_blue_marker)
        val redIcon = ImageProvider.fromResource(this, R.drawable.ic_red_marker)
        clusterCollection.addPlacemarks(createPoints(wayInfo.p!!, StatusEnum.completed), greenIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.p!!, StatusEnum.empty), blueIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.p!!, StatusEnum.breakDown), redIcon, IconStyle())
        clusterCollection.addPlacemarks(createPoints(wayInfo.p!!, StatusEnum.failure), redIcon, IconStyle())
        clusterCollection.addTapListener(this)
        clusterCollection.clusterPlacemarks(60.0, 15)
    }

    private fun createPoints(list: RealmList<WayPointEntity>, status: Int): List<Point> {
        return list.filter { it.status == status }.map {
            Point(it.co?.get(0)!!, it.co!![1]!!)
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
            val clickedPoint = wayInfo.p!!.find {
                it.co!![0]!! == coordinate.latitude && it.co!![1]!! == coordinate.longitude
            }!!
            showClickedPointDetail(clickedPoint).run {
                this.point_detail_start_service.setOnClickListener {
                    val intent = Intent(this@MapActivity, PointServiceActivity::class.java)
                    val itemJson = Gson().toJson(clickedPoint)
                    intent.putExtra("container", itemJson)
                    startActivityForResult(intent, POINT_SERVICE_CODE)
                    hideDialog()
                }

                this.point_detail_fire.setOnClickListener {
                    val intent = Intent(this@MapActivity, ContainerProblemActivity::class.java)
                    intent.putExtra("wayPoint", Gson().toJson(clickedPoint))
                    intent.putExtra("isContainerProblem", false)
                    Log.d(TAG, "startPointProblem: ${Gson().toJson(clickedPoint)}")
                    startActivityForResult(intent, REQUEST_EXIT)
                }
            }
        } catch (e: Exception) {
            toast("Не удалось загрузить")
        }
        return true
    }

    private fun initBottomBehavior() {
        val wayInfo = viewModel.findWayTask()
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        map_behavior_rv.adapter = WayPointAdapter(this, wayInfo.p!!)
        map_behavior_header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val hasNotServedPoint = wayInfo.p!!.any { it.status == StatusEnum.empty }
        if (hasNotServedPoint) {
            map_behavior_send_btn.background = getDrawable(R.drawable.bg_button_red)
            map_behavior_send_btn.text = getString(R.string.finish_way_now)
        } else {
            map_behavior_send_btn.background = getDrawable(R.drawable.bg_button)
            map_behavior_send_btn.text = getString(R.string.finish_way)
        }
        map_behavior_send_btn.setOnClickListener {
            finishWay(hasNotServedPoint)
        }

    }

    private fun finishWay(boolean: Boolean) {
        if (!boolean) {
            completeEnterInfo()
        } else {
            val allReasons = viewModel.findCancelWayReason()
            showFailureFinishWay(allReasons).run {
                this.accept_btn.setOnClickListener {
                    if (!this.reason_et.text.isNullOrEmpty()) {
                        val failureId = allReasons.find { it.problem == this.reason_et.text.toString() }!!.id
                        val body = EarlyCompleteBody(datetime = System.currentTimeMillis() / 1000L, failureId = failureId)
                        loadingShow()
                        viewModel.earlyComplete(AppPreferences.wayTaskId, body)
                            .observe(this@MapActivity, Observer { result ->
                                when (result.status) {
                                    Status.SUCCESS -> {
                                        completeEnterInfo()
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

    private fun completeEnterInfo() {
        showCompleteEnterInfo().run {
            this.accept_btn.setOnClickListener {
                if (this.weight_tg.isChecked || this.volume_tg.isChecked) {
                    val unloadType = if (this.volume_tg.isChecked) 1 else 2
                    val body = CompleteWayBody(finishedAt = System.currentTimeMillis() / 1000L, unloadType = unloadType, unloadValue = "${this.comment_et.text.toString()}.00")
                    loadingShow()
                    viewModel.completeWay(AppPreferences.wayTaskId, body)
                        .observe(this@MapActivity, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    loadingHide()
                                    showSuccessComplete().run {
                                        this.accept_btn.setOnClickListener {
                                            viewModel.clearData()
                                            startActivity(Intent(this@MapActivity, WayTaskActivity::class.java))
                                            AppPreferences.thisUserHasTask = false
                                            finish()
                                        }
                                        this.exit_btn.setOnClickListener {
                                            MyUtil.logout(this@MapActivity)
                                            viewModel.clearData()
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

    override fun startPointService(item: WayPointEntity) {
        val intent = Intent(this, PointServiceActivity::class.java)
        val itemJson = Gson().toJson(item)
        intent.putExtra("container", itemJson)
        startActivityForResult(intent, POINT_SERVICE_CODE)
    }

    override fun startPointProblem(item: WayPointEntity) {
        val intent = Intent(this, ContainerProblemActivity::class.java)
        intent.putExtra("wayPoint", Gson().toJson(item))
        intent.putExtra("isContainerProblem", false)
        Log.d(TAG, "startPointProblem: ${Gson().toJson(item)}")
        startActivityForResult(intent, REQUEST_EXIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POINT_SERVICE_CODE && resultCode == Activity.RESULT_OK) {
            initMapView()
            initBottomBehavior()
        } else if (requestCode == REQUEST_EXIT && resultCode == ActivityResult.pointProblem) {
            initMapView()
            initBottomBehavior()
        }
    }
}