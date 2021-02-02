package ru.smartro.worknote.ui.map

import ClusterIcon
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
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.behavior_points.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.WayPointAdapter
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.response.way_task.WayInfo
import ru.smartro.worknote.service.response.way_task.WayPoint
import ru.smartro.worknote.ui.point_service.PointServiceActivity


class MapActivity : AppCompatActivity(), ClusterListener, ClusterTapListener, UserLocationObjectListener, MapObjectTapListener, WayPointAdapter.ContainerClickListener {
    private val TAG = "MapActivity"
    private val viewModel: MapViewModel by viewModel()
    private lateinit var userLocationLayer: UserLocationLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        viewModel.findWayTaskJsonByUser(AppPreferences.userLogin).observe(this, Observer {
            val wayInfo = Gson().fromJson(it.wayTaskJson, WayInfo::class.java)
            Log.d(TAG, it.wayTaskJson)
            initMapView(wayInfo)
            initBottomBehavior(wayInfo)
        })
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

    private fun initMapView(wayInfo: WayInfo) {
        val clusterizedCollection: ClusterizedPlacemarkCollection =
            map_view.map.mapObjects.addClusterizedPlacemarkCollection(this)
        val imageProvider = ImageProvider.fromResource(this, R.drawable.search_result)
        clusterizedCollection.addPlacemarks(createPoints(wayInfo.points), imageProvider, IconStyle())
        clusterizedCollection.addTapListener(this)
        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    private fun createPoints(list: List<ru.smartro.worknote.service.response.way_task.WayPoint>): List<Point> {
        val pointsArrayList = ArrayList<Point>()
        for (p in list) {
            pointsArrayList.add(Point(p.coordinate[0], p.coordinate[1]))
        }
        return pointsArrayList
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

        return true
    }

    //UserLocation
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.ic_vehicle_png))
        userLocationView.accuracyCircle.isVisible = false
        userLocationLayer.setObjectListener(null)
    }

    override fun onObjectRemoved(p0: UserLocationView) {
        TODO("Not yet implemented")
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        TODO("Not yet implemented")
    }

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {

        return true
    }

    private fun initBottomBehavior(wayInfo: WayInfo) {
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        viewModel.findContainerInfo().observe(this, Observer {
            map_behavior_rv.adapter = WayPointAdapter(this, wayInfo.points as ArrayList<WayPoint>, it)
        })
        map_behavior_header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun startPointService(item: WayPoint) {
        val intent = Intent(this, PointServiceActivity::class.java)
        intent.putExtra("container", item)
        startActivity(intent)
    }
}