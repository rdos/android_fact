package ru.smartro.worknote

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.*
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
import kotlinx.android.synthetic.main.act_map.*
import kotlinx.android.synthetic.main.act_map__bottom_behavior.*
import kotlinx.android.synthetic.main.alert_clear_navigator.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.*
import ru.smartro.worknote.work.net.CancelWayReasonEntity
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.MapActBottomBehaviorAdapter
import ru.smartro.worknote.work.MapActPlatformClickedDtlDialog
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.WorkOrderEntity
import ru.smartro.worknote.work.ac.PERMISSIONS
import ru.smartro.worknote.work.platform_serve.PlatformServeAct
import ru.smartro.worknote.work.ui.DebugAct
import ru.smartro.worknote.work.ui.JournalChatAct
import ru.smartro.worknote.work.ui.PlatformFailureAct
import java.util.*


//todo: тодо:!r_dos
//Двигается карта deselectGeoObject()
class MapAct : ActAbstract(), MapActBottomBehaviorAdapter.PlatformClickListener,
    MapObjectTapListener, UserLocationObjectListener, InertiaMoveListener {


    private var mAcbGotoComplete: AppCompatButton? = null
    private var mAcbComplete: AppCompatButton? = null

    private var mAdapterBottomBehavior: MapActBottomBehaviorAdapter? = null
    private var mMapObjectCollection: MapObjectCollection? = null
    private var mIsAUTOMoveCamera: Boolean = false
    private var mInfoDialog: AlertDialog? = null
    private lateinit var mAcbInfo: AppCompatButton
    private lateinit var mMapMyYandex: MapView
    private val vs: MapViewModel by viewModel()
    private val mWorkOrderFilteredIds: MutableList<Int> = mutableListOf()
    private var mWorkOrderS: List<WorkOrderEntity>? = null
    private var mPlatformS: List<PlatformEntity>? = null
    var drivingModeState = false

    private val REQUEST_EXIT = 41
    private var mIsFirstTime = true

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mDrivingRouter: DrivingRouter
    private lateinit var mDrivingSession: DrivingSession.DrivingRouteListener
//    private lateinit var selectedPlatformToNavigate: Point

    override fun onNewGPS() {
        log("onNewGPS")

        val point = AppliCation().gps()
        if (mIsFirstTime) {
            moveCameraTo(point)
            mIsFirstTime = false
        }
        if (mIsAUTOMoveCamera) {
            moveCameraTo(point)
        }

        val platformNear = vs.baseDat.findPlatformByCoord(point.latitude, point.longitude, point.getAccuracy())

        if (platformNear == null) {
            log("platformNear.is null")
            for ((key, _) in mNotifyMap) {
                App.getAppliCation().cancelNotification(key)
                mNotifyMap.remove(key)
            }
        } else {
//            toast("FFFFFFFFFFFFF${platformNear.srpId}")]
            if (!platformNear.isTypoMiB()) {
                // TODO: !!!r_dos
                showNotificationPlatfrom(platformNear.platformId, platformNear.srpId!!, platformNear.name)
            }
        }

//       platformNear.observe(this, Observer { platformList ->
//        if (!platformNear.isNullOrEmpty()) {
//            if (platformNear.size == 1) {
//                val platform = platformNear[0]
//                toast("FFFFFFFFFFFFF${platform.platformId}")
//                showNotificationPlatfrom(platform.platformId, platform.name)
//            } else {
//                toast("platformList.size=${platformList.size}")
//            }
//        } else {
//            LOGWork("platformList.is null")
//        }
//       })

//        Log.d("LogDistance", "###################")
//
//
//
//        val distanceToPoint = MyUtil.calculateDistance(AppliCation().LocationPOINT, selectedPlatformToNavigate)
////        Log.d("LogDistance", "Distance: $distanceToPoint")
//        if (drivingModeState && distanceToPoint <= MIN_METERS && isOnPointFirstTime) {
//            isOnPointFirstTime = false
//            alertOnPoint().let {
//                it.
    //                dismiss_btn.setOnClickListener {
//                    drivingModeState = false
//                    isOnPointFirstTime = true
//                    clearMapIbjectsDrive()
//                    hideDialog()
//                }
//            }
//        } else {
////            Log.d("LogDistance", "Distance not arrive")
//        }

//
//        Log.d("LogDistance", "Location updated")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!MyUtil.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }


        setContentView(R.layout.act_map)

        mMapMyYandex = findViewById(R.id.map_view)
        val hasWorkOrdersInNotProgress = vs.baseDat.hasWorkOrderInNotProgress()
        if (hasWorkOrdersInNotProgress) {
            showingProgress()
            val extraPramId = getPutExtraParam_ID()
            val workOrderS = vs.baseDat.findWorkOrders_Old(extraPramId)
            getNetDataSetDatabase(workOrderS)
        }
        mMapMyYandex.map.addInertiaMoveListener(this)

        mAcbInfo = findViewById(R.id.acb_act_map__info)
        mAcbInfo.setOnClickListener {
            createInfoDialog({})
        }
        setInfoData()


        val acbLogout = findViewById<AppCompatButton>(R.id.acb_act_map__logout)
        acbLogout.setOnClickListener {
            logout()
        }

        initBottomBehavior()

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mMapMyYandex.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        gotoMyGPS.setOnClickListener {
            try {
                AppliCation().startLocationService(true)
                mIsAUTOMoveCamera = true
                moveCameraTo(AppliCation().gps())
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }


        debug_fab.setOnClickListener {
            startActivityForResult(Intent(this, DebugAct::class.java), -111)
        }
        navigator_toggle_fab.setOnClickListener {
            drivingModeState = false
            navigator_toggle_fab.isVisible = drivingModeState
            clearMapObjectsDrive()
            hideDialog()
        }
        val gotoLogActMapAPIB = findViewById<AppCompatImageButton>(R.id.goto_log__act_map__apib)
        gotoLogActMapAPIB.setOnClickListener {
            startActivity(Intent(this@MapAct, JournalChatAct::class.java))
        }

        mDrivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mDrivingSession = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutesError(p0: Error) {
                toast("Ошибка при построении маршрута")
            }

            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                routes.forEach { getMapObjectsDrive()?.addPolyline(it.geometry) }
            }

        }

        //todo:  R_dos!!! modeSyNChrON_off(false)
        paramS.isModeSYNChrONize = true
        AppliCation().startWorkER()
        AppliCation().startLocationService()
//        setDevelMode()
    }

    private var mMapObjectsDrive: MapObjectCollection? = null
    private fun clearMapObjectsDrive() {
        mMapObjectsDrive?.clear()
        mMapObjectsDrive = null
    }

    private fun getMapObjectsDrive(): MapObjectCollection? {
        if (mMapObjectsDrive == null) {
            mMapObjectsDrive = mMapMyYandex.map.mapObjects.addCollection()
        }
        return mMapObjectsDrive
    }

    private fun onRefreshData() {
        Log.w(TAG, "onRefreshData.init")
        mWorkOrderS = getActualWorkOrderS(true)
        mPlatformS = getActualPlatformS(true)
        onRefreshMap()
        onRefreshBottomBehavior()
        setInfoData()
        Log.w(TAG, "onRefreshData.end")
    }

    private fun setInfoData() {
        val workOrders = getActualWorkOrderS()
        var platformCnt = 0
        var platformProgress = 0
        for(workOrder in workOrders) {
            platformCnt += workOrder.cnt_platform
            platformProgress += workOrder.cnt_platform - workOrder.cnt_platform_status_new
        }
        mAcbInfo.text = "$platformProgress / $platformCnt"
    }

    private fun getActualWorkOrderS(isForceMode: Boolean = false, isFilterMode: Boolean = true): List<WorkOrderEntity> {
        if (mWorkOrderS == null || isForceMode) {
            mWorkOrderS = vs.baseDat.findWorkOrders(isFilterMode)
            if (mWorkOrderS?.isEmpty() == true) {
                mWorkOrderS = vs.baseDat.findWorkOrders(false)
            }
        }
        return mWorkOrderS!!
    }


    private fun getActualPlatformS(isForceMode: Boolean = false): List<PlatformEntity> {
        if (mPlatformS == null || isForceMode) {
            val workOrderS = getActualWorkOrderS(isForceMode)
            val newPlatformS = mutableListOf<PlatformEntity>()
            workOrderS.forEach {
                newPlatformS.addAll(it.platforms)
            }
        // TODO: R_dos!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        /**@kotlin.internal.InlineOnly
        public inline fun <T> compareByDescending(crossinline selector: (T) -> Comparable<*>?): Comparator<T> =
            Comparator { a, b -> compareValuesBy(b, a, selector) }*/
            newPlatformS.sortWith(compareBy { it.updateAt  })
            mPlatformS = newPlatformS
//            mPlatformS = vs.baseDat.findPlatforms(getWorkOrderSFilter())
        }
        return mPlatformS!!
    }

    private fun getNetDataSetDatabase(workOrderS: List<WorkOrderEntity>) {
        if (workOrderS.isEmpty()) {
            hideProgress()
            return
        }
        saveFailReason()
        saveCancelWayReason()
        saveBreakDownTypes()
//                    val hand = Handler(Looper.getMainLooper())
        for (workOrder in workOrderS) {
            logSentry(workOrder.id.toString())
            progressNetData(workOrder, workOrderS.size)
        }
    }

    private fun saveBreakDownTypes() {
        vs.networkDat.getBreakDownTypes().observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    // TODO: ПО голове себе постучи
                    Log.d(TAG, "saveBreakDownTypes. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                else -> Log.d(TAG, "saveBreakDownTypes:")
            }

        }
    }

    private fun saveFailReason() {
        Log.i(TAG, "saveFailReason.before")
        vs.networkDat.getFailReason().observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveFailReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        }
    }

    private fun saveCancelWayReason() {
        Log.d(TAG, "saveCancelWayReason.before")
        vs.networkDat.getCancelWayReason().observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveCancelWayReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    Log.d(TAG, "saveCancelWayReason. Status.ERROR")
                    toast(result.msg)
                }
                else -> {
                    oops()
                }
            }
        }
    }

    private val resultStatusList = mutableListOf<Status>()
    private fun progressNetData(workOrder: WorkOrderEntity, workOrderSize: Int) {
        Log.d(TAG, "acceptProgress.before")
        vs.networkDat.progress(workOrder.id, ProgressBody(MyUtil.timeStampInSec())).observe(this) { result ->
            resultStatusList.add(result.status)
            modeSyNChrON_off(false)
            when (result.status) {
                Status.SUCCESS -> {
                    logSentry("acceptProgress Status.SUCCESS ")
                    vs.baseDat.setProgressData(workOrder)
                    modeSyNChrON_off(false)
                    hideProgress()
                }
                else -> {
                    logSentry("acceptProgress Status.ERROR")
                    toast(result.msg)
                    vs.baseDat.setNextProcessDate(workOrder)
                }
            }
            if (workOrderSize == resultStatusList.size) {
                onRefreshData()
                hideProgress()
            }
        }

    }

    private fun gotoComplete() {
        val lastPlatforms =  vs.findLastPlatforms()
        if (lastPlatforms.isEmpty()) {
            completeWorkOrders()
        } else {
            gotoSynchronize(lastPlatforms)
        }
    }


    private fun gotoSynchronize(lastPlatforms: List<PlatformEntity>) {
        showingProgress()
        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        val gps = AppliCation().gps()

        val synchronizeBody = SynchronizeBody(
            paramS().wayBillId,
            gps.PointTOBaseData(),
            deviceId,
            gps.PointTimeToLastKnowTime_SRV(),
            lastPlatforms)

        vs.networkDat.sendLastPlatforms(synchronizeBody).observe(this) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    completeWorkOrders()
                }
                Status.ERROR -> {
                    toast(result.msg)
                    hideProgress()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    hideProgress()
                }
            }
        }
    }


    private fun completeWorkOrders() {
        hideInfoDialog()
        val intent = Intent(this, TerminateAct::class.java)
        startActivity(intent)
    }

    private fun showInfoDialog() {
        mInfoDialog?.show()
    }

    //todo:::Гавно кодика?*
    private fun createInfoDialog(next: () -> Any) {
        lateinit var result: AlertDialog
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.act_map__workorder_info, null)
        // TODO:
        val workOrderS = getActualWorkOrderS(true, isFilterMode = false)
//            var infoText = "**Статистика**\n"
        val rvInfo = view.findViewById<RecyclerView>(R.id.rv_act_map__workorder_info)
        mAcbComplete = view.findViewById(R.id.acb_act_map__workorder_info__complete)
        mAcbComplete?.setOnClickListener {
            vs.baseDat.setWorkOrderIsShowForUser(workOrderS)
            gotoComplete()
        }
        setAcbCompleteText(workOrderS)

        rvInfo.layoutManager = LinearLayoutManager(this)
        val infoAdapter = InfoAdapter(workOrderS)
        rvInfo.adapter = infoAdapter

        builder.setView(view)
        result = builder.create()
        result.setOnCancelListener {
            val workorder: Unit = vs.baseDat.setWorkOrderIsShowForUser(workOrderS)
            next()
            onRefreshData()
        }
        try {
            val window: Window? = result?.window
            val wlp: WindowManager.LayoutParams = window!!.attributes

            wlp.gravity = Gravity.TOP
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
        } catch (ex: Exception) {
            Log.e(TAG, "eXthr.message", ex)
        }
        mInfoDialog = result
        mInfoDialog?.show()
    }

    private fun hideInfoDialog() {
        try {
            mInfoDialog?.dismiss()
        } catch (ex: Exception) {
            // TODO: 02.11.2021
            Log.e(TAG, "hideInfoDialog", ex)
        }
    }


    private fun onRefreshBottomBehavior() {
        val platforms = getActualPlatformS()
        mAdapterBottomBehavior?.updateItemS(platforms)
        if (getActualWorkOrderS().size <= 1) {
            mAcbGotoComplete?.text = "Завершить маршрут"
        } else {
            mAcbGotoComplete?.text ="К завершению маршрута"
        }
    }
    private fun initBottomBehavior() {
        val platforms = getActualPlatformS()
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)

        bottomSheetBehavior.expandedOffset = 100
        val rvBehavior = findViewById<RecyclerView>(R.id.map_behavior_rv)
        mAdapterBottomBehavior = MapActBottomBehaviorAdapter(this, platforms, mWorkOrderFilteredIds)
        rvBehavior.adapter = mAdapterBottomBehavior
//        rvBehavior.adapter.notifyDataSetChanged()
        act_map__bottom_behavior__header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        mAcbGotoComplete = findViewById<AppCompatButton>(R.id.acb_act_map__bottom_behavior__gotocomplete)
        mAcbGotoComplete?.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            createInfoDialog(){}
            if (getActualWorkOrderS().size <= 1) {
                gotoComplete()
            }
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun startPlatformService(item: PlatformEntity) {
        if (App.getAppliCation().gps().isThisPoint(item.coordLat, item.coordLong)) {
            gotoNextAct(item)
        } else {
            showAlertPlatformByPoint().let { view ->
                val btnOk = view.findViewById<Button>(R.id.act_map__dialog_platform_clicked_dtl__alert_by_point__ok)
                btnOk.setOnClickListener {
                    hideDialog()
                    gotoNextAct(item)
                }
            }
        }

    }

    //todo: https://www.gamemodd.com/uploads/posts/2017-05/1495207495_1.6-m4a1-retexture2.jpg
    //тодо)) код фанатика m4 из cs)))
    private fun gotoNextAct(plaform: PlatformEntity, todoParamREQUEST_EXIT: Int = Inull) {
        Log.d("GOTONEXTACT platform ::: ", "${plaform.platformId} : progress ${plaform.isWorkOrderProgress} : success ${plaform.isWorkOrderComplete}")
        val intent = Intent(this, if(todoParamREQUEST_EXIT == Inull) PlatformServeAct::class.java else PlatformFailureAct::class.java)
        intent.putExtra("platform_id", plaform.platformId)

        if (todoParamREQUEST_EXIT == Inull) {
            startActivity(intent)
        } else {
            startActivityForResult(intent, todoParamREQUEST_EXIT)
        }
    }

    override fun startPlatformProblem(plaform: PlatformEntity) {
        hideDialog()
        gotoNextAct(plaform, REQUEST_EXIT)
    }

    override fun moveCameraPlatform(point: PoinT) {
        mIsAUTOMoveCamera = false
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        moveCameraTo(point)
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
//            getMapObjCollection().clear()
            clearMapObjectsDrive()
            vs.buildMapNavigator(AppliCation().gps(), checkPoint, mDrivingRouter, mDrivingSession)
            drivingModeState = true
            navigator_toggle_fab.isVisible = drivingModeState
            hideDialog()
            val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            moveCameraTo(AppliCation().gps())
        } catch (ex: Exception) {
            Log.e(TAG, "buildNavigator", ex)
            toast(getString(R.string.error_build_way))
        }

    }

    private fun moveCameraTo(pont: PoinT) {
        mMapMyYandex.map.move(
            CameraPosition(pont, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null)
    }

//    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
//        logSentry("onLocationStatusUpdated ${locationStatus.name.toStr()}")
//        when (locationStatus) {
//            LocationStatus.NOT_AVAILABLE -> Log.d("LogDistance", "GPS STOP")
//            LocationStatus.AVAILABLE -> Log.d("LogDistance", "GPS START")
//
//        }
//    }
//

//    private fun rotateCompass(azimut: Float) {
//        val azimuth = Math.round(Math.toDegrees(azimut.toDouble())).toFloat()
//        val currentLoc = AppliCation().GPS().Location!!
//
//        val target = Location("")
//        target.setLatitude(34.000)
//        target.setLongitude(34.000)
//        var bearing: Float = currentLoc.bearingTo(target) // (it's already in degrees)
//        if (bearing < 0) {
//            bearing = bearing + 360
//        }
//        var direction = (bearing - azimuth)
//
//        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
//        if (direction < 0) {
//            direction = direction + 360
//        }
//        toast("" + direction)
//        rotateImageView(imgCompass, R.drawable.pin_finder, direction)
//    }

    val mNotifyMap = mutableMapOf<Int, Long>()
    private fun showNotificationPlatfrom(platformId: Int?, srpId: Int, string: String?) {
        val intent = Intent(this, PlatformServeAct::class.java)

        // TODO: !!?R_dos
        intent.putExtra("srpId", srpId)
        intent.putExtra("platform_id", platformId)
        intent.putExtra("mIsServeAgain", false)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
//        (;:)
        if (mNotifyMap.containsKey(srpId)) {
            return
        }
        mNotifyMap[srpId] = Lnull
        AppliCation().showNotificationForce(pendingIntent,
            "Контейнерная площадка №${srpId}",
            "Вы подъехали к контейнерной площадке",
            "Начать обслуживание ",
            srpId,
            NOTIFICATION_CHANNEL_ID__MAP_ACT
        )
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("r_dos//onActivityResult.requestCode=${requestCode}")
        log("r_dos//onActivityResult.resultCode=${resultCode}")

    }
//    override fun onLocationUpdated(location: Location) {
////        Log.d("LogDistance", "###################")
//        currentLocation = location
//        LOGWork("onLocationUpdatedaccuracy.${currentLocation.accuracy}")
//        paramS().currentCoordinate = "${location.position.longitude}#${location.position.latitude}"
//        paramS().currentCoordinateAccuracy = location.accuracy.toString()
//        val distanceToPoint = MyUtil.calculateDistance(location.position, selectedPlatformToNavigate)
////        Log.d("LogDistance", "Distance: $distanceToPoint")
//        if (drivingModeState && distanceToPoint <= MIN_METERS && isOnPointFirstTime) {
//            isOnPointFirstTime = false
//            alertOnPoint().let {
//                it.dismiss_btn.setOnClickListener {
//                    drivingModeState = false
//                    isOnPointFirstTime = true
//                    clearMapIbjectsDrive()
//                    hideDialog()
//                }
//            }
//        } else {
////            Log.d("LogDistance", "Distance not arrive")
//        }
//        if (firstTime) {
//            moveCameraToUser(location)
//            firstTime = false
//        }
////        Log.d("LogDistance", "Location updated")
//    }

    private fun onRefreshMap() {
        val platforms = getActualPlatformS()
        mMapObjectCollection?.clear()
        try {
            mMapObjectCollection = mMapMyYandex.map.mapObjects.addCollection()
        } catch (ex: Exception) {
            Log.e(TAG, "eXthr.message", ex)
            // TODO: :)!!!
            mMapObjectCollection = null
        }

        mMapObjectCollection?.removeTapListener(this)
        addPlaceMarks(this, mMapObjectCollection, platforms)
        mMapObjectCollection?.addTapListener(this)
    }

    private fun addPlaceMarks(context: Context, mapObjectCollection: MapObjectCollection?, platforms: List<PlatformEntity>) {
        for(platform in platforms) {
            val iconProvider = getIconViewProvider(context, platform)
            val pointYandex = Point(platform.coords[0]!!, platform.coords[1]!!)
            mapObjectCollection?.addPlacemark(pointYandex, iconProvider)
        }
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeMark = mapObject as PlacemarkMapObject
        val coordinate = placeMark.geometry

        val clickedPlatform = vs.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
        if(clickedPlatform == null) {
            toast("Платформа не найдена")
            return false
        }
        Log.w("RRRR", "onMapObjectTap")
        val platformClickedDtlDialog = MapActPlatformClickedDtlDialog(clickedPlatform, coordinate)
        platformClickedDtlDialog.show(supportFragmentManager, "PlaceMarkDetailDialog")
        return true
    }


    private fun getIconViewProvider (_context: Context, _platform: PlatformEntity): ViewProvider {
        val result = layoutInflater.inflate(R.layout.map_activity__iconmaker, null)
        val iv = result.findViewById<ImageView>(R.id.map_activity__iconmaker__imageview)
        iv.setImageDrawable(ContextCompat.getDrawable(_context, _platform.getIconDrawableResId()))
        val tv = result.findViewById<TextView>(R.id.map_activity__iconmaker__textview)
        tv.isVisible = false
        if (_platform.isOrderTimeWarning()) {
            val orderTime = _platform.getOrderTimeForMaps()
            if (orderTime.isShowForUser()) {
                tv.text = orderTime
                tv.setTextColor(_platform.getOrderTimeColor(this))
                tv.isVisible = true
            }
        }

        //фильрация
        if (_platform.workOrderId in mWorkOrderFilteredIds) {
            iv.alpha = 0.1f
            result.alpha = 0.1f
            tv.alpha = 0.1f
        }

        return ViewProvider(result)
    }


//    private fun drawSimpleBitmap(number: String):Bitmap{
//        val picSize = 100.0f// {нужный вам размер изображения}
//        val bitmap =  Bitmap.createBitmap(picSize.toInt(), picSize.toInt(), Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        //        // отрисовка плейсмарка
//        val paint = Paint()
//        paint.color = Color.GREEN
//        paint.setStyle(Paint.Style.FILL)
//        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint)
//        //        // отрисовка текста
//        paint.setColor(Color.WHITE)
//        paint.setAntiAlias(true)
//        paint.setTextSize(30f)
//        canvas.drawText(number, 0f,picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
//        return bitmap
//    }


/** РИСУЕМ МАШИНКУ, нормальную ic_truck_icon.png*/
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.accuracyCircle.isVisible =true
//        userLocationLayer.setObjectListener(this)
    }

    override fun onObjectRemoved(p0: UserLocationView) {
//        TODO("Not yet implemented")
        log("onObjectRemoved")
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
//        TODO("Not yet implemented")
        log("onObjectUpdated")

    }


    /**
//        val source = BitmapFactory.decodeResource(context.resources, R.drawable.your_icon_name)
// создаем mutable копию, чтобы можно было рисовать поверх
// создаем mutable копию, чтобы можно было рисовать поверх
//        val bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
// инициализируем канвас
// инициализируем канвас
//        val canvas = Canvas(bitmap)
// рисуем текст на канвасе аналогично примеру выше

     */

    open class MapViewModel(application: Application) : BaseViewModel(application) {

        fun findLastPlatforms() =
            baseDat.findLastPlatforms()

        fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity? {
            return baseDat.findPlatformByCoordinate(lat, lon)
        }

        fun findCancelWayReason(): List<CancelWayReasonEntity> {
            return baseDat.findCancelWayReasonEntity()
        }

        fun findCancelWayReasonByValue(reason: String): Int {
            return baseDat.findCancelWayReasonIdByValue(reason)
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

    override fun onStart() {
        super.onStart()
        Log.w(TAG, "r_dos/onStart.before")
        mMapMyYandex.onStart()
        MapKitFactory.getInstance().onStart()
        Log.w(TAG, "r_dos/onStart.after")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "r_dos/onResume.before")
        onRefreshData()
        Log.e(TAG, "r_dos/onResume.after")
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapMyYandex.onStop()
        MapKitFactory.getInstance().onStop()
    }

    inner class InfoAdapter(private var p_workOrderS: List<WorkOrderEntity>) :
        RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.act_map__workorder_info__rv_item, parent, false)
            return InfoViewHolder(view)
        }

//        //todo:r_dos!
//        private fun saveCheckBoxInWorkorderEntity(workOrderId: Int, isChecked: Boolean) {
//
//        }
//        fun setNewData(workOrderS : List<WorkOrderEntity>){
//
//        }

        override fun getItemCount(): Int {
            return p_workOrderS.size
        }

        override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
            val workOrder = p_workOrderS[position]
            holder.tvPlatformCnt.text = "Площадки - ${workOrder.cnt_platform}"
            holder.tvPlatformSuccess.text = workOrder.cnt_platform_status_success.toString()
            holder.tvPlatformError.text = workOrder.cnt_platform_status_error.toString()
            holder.tvPlatformProgress.text = (workOrder.cnt_platform - (workOrder.cnt_platform_status_success + workOrder.cnt_platform_status_error)).toString()

            holder.tvContainerCnt.text = "Контейнеры - ${workOrder.cnt_container}"
            holder.tvContainerSuccess.text = workOrder.cnt_container_status_success.toString()
            holder.tvContainerError.text = workOrder.cnt_container_status_error.toString()
            //todo: линию незаметил)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
            holder.tvContainerProgress.text = (workOrder.cnt_container - (workOrder.cnt_container_status_success + workOrder.cnt_container_status_error)).toString()
            val checkBox = holder.accbCheckBox
            checkBox.text = "${workOrder.id} ${workOrder.name}"
//            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = workOrder.isShowForUser
            checkBox.setOnCheckedChangeListener { buttonView, b ->
                workOrder.isShowForUser = buttonView.isChecked
                setAcbCompleteText(p_workOrderS)
//                mviewWorkorderInfo?.post {
//                    notifyItemChanged(position)
//                }

            }

        }

        fun getItemS(): List<WorkOrderEntity> {
            return p_workOrderS
        }

        inner class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvPlatformCnt: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__platform_cnt)
            }
            val tvPlatformSuccess: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__platform_success)
            }
            val tvPlatformError: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__platform_error)
            }
            val tvPlatformProgress: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__platform_progress)
            }

            val tvContainerCnt: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__container_cnt)
            }
            val tvContainerSuccess: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__container_success)
            }
            val tvContainerError: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__container_error)
            }
            val tvContainerProgress: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__container_progress)
            }
            val accbCheckBox: AppCompatCheckBox by lazy {
                val checkBox = itemView.findViewById<AppCompatCheckBox>(R.id.act_map__workorder_info__checkbox)
                checkBox
            }
        }
    }

    private fun setAcbCompleteText(workOrderS: List<WorkOrderEntity>) {
        for (workorder in workOrderS) {
            if (workorder.isShowForUser) {
                mAcbComplete?.text = "Завершить маршрут"
                mAcbComplete?.background = ContextCompat.getDrawable(this, R.drawable.bg_button)
                mAcbComplete?.isEnabled = true
                return
            }
        }
        mAcbComplete?.isEnabled = false
        mAcbComplete?.text = "Выберите задание"
        mAcbComplete?.background = ContextCompat.getDrawable(this, R.drawable.bg_button_gray)
    }

    override fun onStart(p0: Map, p1: CameraPosition) {
        mIsAUTOMoveCamera = false
    }

    override fun onCancel(p0: Map, p1: CameraPosition) {
        this as InertiaMoveListener
        Log.d(TAG, "onCancel")
    }

    override fun onFinish(p0: Map, p1: CameraPosition) {
        Log.d("AAAA", "onFinish")
//        this as InertiaMoveListener
    }

}
