package ru.smartro.worknote

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.ui_view.ViewProvider
import ru.smartro.worknote.ac.PoinT
import ru.smartro.worknote.log.todo.PlatformEntity

data class PlatformIconConfig(
    val iconResId: Int,
    val isActive: Boolean,
    val isOrderTimeShowForUser: Boolean,
    val orderTimeWarning: String?,
    val orderTimeColor: Int?
)

interface MapListener {
    fun onPlatformTap(platformId: Int)
    fun onInertiaStart()
}

class MapHelper(private val mapView: MapView, private val listener: MapListener):
    MapObjectTapListener,
    UserLocationObjectListener,
    InertiaMoveListener,
    DrivingSession.DrivingRouteListener {

    private val mapIconViewProviderS: HashMap<PlatformIconConfig, ViewProvider> = hashMapOf()
    private val platformIconConfigS: HashMap<Int, PlatformIconConfig> = hashMapOf()
    private val platformMapObjectS: HashMap<Int, MapObject> = hashMapOf()

    private var lastActivePlatformMapObject: PlacemarkMapObject? = null

    private var mapObjectCollection: MapObjectCollection? = null

    private var mMapObjectsDrive: MapObjectCollection? = null

    private var userLocationLayer: UserLocationLayer
    private var mDrivingRouter: DrivingRouter
    private var mDrivingSession: DrivingSession.DrivingRouteListener

    private var context: Context = mapView.context

    init {

        mapView.map.addInertiaMoveListener(this)

        mapObjectCollection = mapView.map.mapObjects.addCollection()
        mapObjectCollection!!.addTapListener(this)

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        mDrivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mDrivingSession = this

        LOG.warn("r_dos/onStart.before")

        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placemark = mapObject as PlacemarkMapObject
        val platformId = mapObject.userData as Int

        if(lastActivePlatformMapObject != null) {
            val lastActiveId = lastActivePlatformMapObject!!.userData as Int
            if(lastActiveId != platformId) {
                val inactiveIcon = getInactiveIcon(lastActiveId)
                if(inactiveIcon != null) {
                    lastActivePlatformMapObject!!.setView(inactiveIcon)
                }
            }
        }


        val newIcon = getActiveIcon(platformId)
        if(newIcon != null) {
            placemark.setView(newIcon)
        }

        lastActivePlatformMapObject = placemark

        listener.onPlatformTap(platformId)

        return true
    }

    private fun getActiveIcon(platformId: Int): ViewProvider? {
        val candidate = platformIconConfigS[platformId]
        val iconView = if(candidate != null) {
            generateViewProvider(candidate, true)
        } else {
            return null
        }
        return iconView
    }

    private fun getInactiveIcon(platformId: Int): ViewProvider? {
        val candidate = platformIconConfigS[platformId]
        val iconView = if(candidate != null) {
            generateViewProvider(candidate, false)
        } else {
            return null
        }
        return iconView
    }

    ///  TODO ::: relatively big
    private fun getIconViewProvider(platform: PlatformEntity, isActiveMode: Boolean = false): ViewProvider {

        val isOrderTimeShowForUser = platform.isOrderTimeWarning() && platform.getOrderTimeForMaps().isShowForUser()

        val iconData = PlatformIconConfig(
            iconResId = platform.getIconFromStatus(),
            isActive = isActiveMode,
            isOrderTimeShowForUser,
            orderTimeWarning = if(isOrderTimeShowForUser) platform.getOrderTimeForMaps() else null,
            orderTimeColor = if(isOrderTimeShowForUser) platform.getOrderTimeColor(context) else null
        )
        platformIconConfigS.put(platform.platformId, iconData)

        val candidate = mapIconViewProviderS.get(iconData)
        if(candidate != null) {
            return candidate
        }

        val viewProvider = generateViewProvider(iconData, isActiveMode)

        mapIconViewProviderS.put(iconData, viewProvider)

        return viewProvider
    }

    private fun generateViewProvider(iconData: PlatformIconConfig, isActiveMode: Boolean): ViewProvider {
        val result = LayoutInflater.from(context).inflate(R.layout.map_activity__iconmaker, null)
        val iv = result.findViewById<ImageView>(R.id.map_activity__iconmaker__imageview)
        iv.setImageDrawable(ContextCompat.getDrawable(context, iconData.iconResId))

        val tv = result.findViewById<TextView>(R.id.map_activity__iconmaker__textview)
        if (isActiveMode) {
            result.findViewById<View>(R.id.v__map_activity__iconmaker__bg_active).visibility = View.VISIBLE
        } else {
            result.findViewById<View>(R.id.v__map_activity__iconmaker__bg_active).visibility = View.GONE
        }

        tv.isVisible = false

        if (iconData.isOrderTimeShowForUser) {
            tv.text = iconData.orderTimeWarning!!
            tv.setTextColor(iconData.orderTimeColor!!)
            tv.isVisible = true
        }

        return ViewProvider(result)
    }

    fun buildMapNavigator(
        point: Point,
        checkPoint: Point
    ) {
        clearMapObjectsDrive()
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
        mDrivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, mDrivingSession)
    }

    fun clearMapObjectsDrive() {
        try {
            LOG.debug("CLEARED::::!!!!")
            mMapObjectsDrive?.clear()
        } catch (ex: Exception) {
            LOG.warn(ex.stackTraceToString())
        }
        mMapObjectsDrive = null
    }

    private fun getMapObjectsDrive(): MapObjectCollection? {
        if (mMapObjectsDrive == null) {
            mMapObjectsDrive = mapView.map.mapObjects.addCollection()
        }
        return mMapObjectsDrive
    }

    fun setPlatforms(platformS: List<PlatformEntity>) {
        val lastActivePlatformId = lastActivePlatformMapObject?.userData
        lastActivePlatformMapObject = null

        mapObjectCollection?.clear()
        platformMapObjectS.clear()

        for(pl in platformS) {
            val candidate = platformMapObjectS[pl.platformId]
            if(candidate != null) {
                LOG.debug("TEST ::: platformIdS.containsKey")
                return
            }
            val mapObject = mapObjectCollection!!.addPlacemark(
                Point(pl.coordLat, pl.coordLong),
                getIconViewProvider(pl)
            )

            mapObject.userData = pl.platformId

            platformMapObjectS[pl.platformId] = mapObject
        }

        if(lastActivePlatformId != null) {
            setActivePlatform(lastActivePlatformId as Int)
        }
    }

    fun setActivePlatform(platformId: Int) {
        val candidate = platformMapObjectS[platformId] as PlacemarkMapObject?
        if(candidate == null) {
            LOG.debug("candidate == null")
            return
        }

        if(lastActivePlatformMapObject != null) {
            val lastActiveId = lastActivePlatformMapObject!!.userData as Int
            if(lastActiveId == platformId)
                return

            val inactiveIcon = getInactiveIcon(lastActiveId)
            if(inactiveIcon != null) {
                lastActivePlatformMapObject!!.setView(inactiveIcon)
            }
        }

        val newIcon = getActiveIcon(platformId)
        if(newIcon != null) {
            candidate.setView(newIcon)
        }

        lastActivePlatformMapObject = candidate
    }

    fun moveCameraTo(pont: PoinT) {
        LOG.debug("before pont.latitude=${pont.latitude} pont.long=${pont.longitude}")
        mapView.map.move(
            CameraPosition(pont, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )
    }

    override fun onStart(p0: Map, p1: CameraPosition) {
        listener.onInertiaStart()
    }

    override fun onCancel(p0: Map, p1: CameraPosition) {

    }

    override fun onFinish(p0: Map, p1: CameraPosition) {

    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.accuracyCircle.isVisible = true
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        routes.forEach { getMapObjectsDrive()?.addPolyline(it.geometry) }
    }

    override fun onDrivingRoutesError(p0: Error) {
        toast("Ошибка при построении маршрута")
    }
}
