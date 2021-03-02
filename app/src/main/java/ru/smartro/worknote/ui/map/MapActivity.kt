package ru.smartro.worknote.ui.map

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.behavior_points.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayPointAdapter
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.database.entity.way_task.WayPointEntity
import ru.smartro.worknote.service.database.entity.way_task.WayTaskEntity
import ru.smartro.worknote.ui.point_service.PointServiceActivity
import ru.smartro.worknote.util.ClusterIcon
import ru.smartro.worknote.util.StatusEnum


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener, UserLocationObjectListener, MapObjectTapListener, WayPointAdapter.ContainerClickListener {
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

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        // клик по точке на карте
        //TODO("Not yet implemented")
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
    }

    override fun startPointService(item: WayPointEntity) {
        val intent = Intent(this, PointServiceActivity::class.java)
        val itemJson = Gson().toJson(item)
        intent.putExtra("container", itemJson)
        startActivityForResult(intent, POINT_SERVICE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POINT_SERVICE_CODE && resultCode == Activity.RESULT_OK) {
            initMapView()
            initBottomBehavior()
        }
    }
}