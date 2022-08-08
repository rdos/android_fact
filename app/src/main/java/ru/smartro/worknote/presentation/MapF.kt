package ru.smartro.worknote.presentation

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Gravity
import android.view.Window
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.InertiaMoveListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.PERMISSIONS
import ru.smartro.worknote.R
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.NOTIFICATION_CHANNEL_ID__MAP_ACT
import ru.smartro.worknote.Lnull
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.awORKOLDs.extensions.hideDialog
import ru.smartro.worknote.awORKOLDs.extensions.showAlertPlatformByPoint
import ru.smartro.worknote.awORKOLDs.extensions.warningClearNavigator
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.platform_serve.PlatformServeSharedViewModel
import ru.smartro.worknote.toast
import ru.smartro.worknote.utils.getActivityProperly
import ru.smartro.worknote.work.*

class MapF: AFragment() , MapActBottomBehaviorAdapter.PlatformClickListener,
    MapObjectTapListener, UserLocationObjectListener, InertiaMoveListener {


    private var acibNavigatorToggle: AppCompatImageButton? = null
    private var clMapBehavior: ConstraintLayout? = null
    private var mAcbGotoComplete: AppCompatButton? = null

    private var mAdapterBottomBehavior: MapActBottomBehaviorAdapter? = null
    private var mMapObjectCollection: MapObjectCollection? = null
    private var mIsAUTOMoveCamera: Boolean = false
    private var mInfoDialog: AlertDialog? = null
    private lateinit var mAcbInfo: AppCompatButton
    private lateinit var mMapMyYandex: MapView
    private val viewModel: PlatformServeSharedViewModel by activityViewModels()
    private val mWorkOrderFilteredIds: MutableList<Int> = mutableListOf()
    private var mWorkOrderS: List<WorkOrderEntity>? = null
    private var mPlatformS: List<PlatformEntity>? = null
    var drivingModeState = false

    private var mIsFirstTime = true

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mDrivingRouter: DrivingRouter
    private lateinit var mDrivingSession: DrivingSession.DrivingRouteListener
    private lateinit var selectedPlatformToNavigate: Point


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

        val platformNear = viewModel.baseDat.findPlatformByCoord(point.latitude, point.longitude, point.getAccuracy())

        if (platformNear == null) {
            log("platformNear.is null")
            for ((key, _) in mNotifyMap) {
                AppliCation().cancelNotification(key)
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

//        log.debug("###################")
//
//
//
//        val distanceToPoint = MyUtil.calculateDistance(AppliCation().LocationPOINT, selectedPlatformToNavigate)
////        log.debug("Distance: $distanceToPoint")
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
////            log.debug("Distance not arrive")
//        }

//
//        log.debug("Location updated")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(getAct(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getAct(), PERMISSIONS, 1)
        }

        mMapMyYandex = view.findViewById(R.id.map_view)
        val hasWorkOrdersInNotProgress = viewModel.baseDat.hasWorkOrderInNotProgress()
        if (hasWorkOrdersInNotProgress) {
            showingProgress()
            val extraPramId = getAct().getPutExtraParam_ID()
            val workOrderS = viewModel.baseDat.findWorkOrders_Old(extraPramId)
            getNetDataSetDatabase(workOrderS)
        }
        mMapMyYandex.map.addInertiaMoveListener(this)

        mAcbInfo = view.findViewById(R.id.acb_f_map__info)
        mAcbInfo.setOnClickListener {
            createInfoDialog({})
        }
        setInfoData()


        val acbLogout = view.findViewById<AppCompatButton>(R.id.acb_f_map__logout)
        acbLogout.setOnClickListener {
            getAct().logout()
        }

        initBottomBehavior(view)

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mMapMyYandex.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        if(viewModel.mPlatformEntity.value != null) {
            val lat = viewModel.mPlatformEntity.value!!.coordLat
            val long = viewModel.mPlatformEntity.value!!.coordLong
            moveCameraTo(PoinT(lat, long))
        }

        val fabGotoMyGPS = view.findViewById<FloatingActionButton>(R.id.fab_f_map__goto_my_gps)
        fabGotoMyGPS.setOnClickListener {
            try {
                AppliCation().startLocationService(true)
                mIsAUTOMoveCamera = true
                moveCameraTo(AppliCation().gps())
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

        val fabDebug = view.findViewById<FloatingActionButton>(R.id.fab_f_map__debug)
        fabDebug.setOnClickListener {
            navigateMain(R.id.DebugFragment, null)
//            startActivity(Intent(getAct(), DebugAct::class.java))
        }

        acibNavigatorToggle = view.findViewById<AppCompatImageButton>(R.id.acib__f_map__navigator_toggle)
        acibNavigatorToggle?.setOnClickListener {
            drivingModeState = false
            acibNavigatorToggle?.isVisible = drivingModeState
            clearMapObjectsDrive()
            hideDialog()
        }
        val acibGotoLogActMapAPIB = view.findViewById<AppCompatImageButton>(R.id.goto_log__f_map__apib)
        acibGotoLogActMapAPIB.setOnClickListener {
            navigateMain(R.id.JournalChatFragment, null)
        //            startActivity(Intent(getAct(), JournalChatAct::class.java))
        }

        mDrivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mDrivingSession = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
                toast("Ошибка при построении маршрута")
            }

            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                routes.forEach { getMapObjectsDrive()?.addPolyline(it.geometry) }
            }



        }
        log.warn( "r_dos/onStart.before")
        mMapMyYandex.onStart()
        MapKitFactory.getInstance().onStart()
        log.warn( "r_dos/onStart.after")
        //todo:  R_dos!!! modeSyNChrON_off(false)
        paramS().isModeSYNChrONize = true
        AppliCation().startWorkER()
        AppliCation().startLocationService()
//        setDevelMode()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (clMapBehavior != null) {
            val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
        }
        navigateClose()
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
        log.warn( "onRefreshData.init")
        mWorkOrderS = getActualWorkOrderS(true)
        mPlatformS = getActualPlatformS(true)
        val platformSWithQueryText = onRefreshBottomBehavior(mPlatformS!!)
        onRefreshMap(platformSWithQueryText)
        setInfoData()
        log.warn( "onRefreshData.end")
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
            mWorkOrderS = viewModel.baseDat.findWorkOrders(isFilterMode)
            if (mWorkOrderS?.isEmpty() == true) {
                mWorkOrderS = viewModel.baseDat.findWorkOrders(false)
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
            newPlatformS.sortWith(compareBy { (it.updateAt) })
            newPlatformS.sortWith(compareBy { (it.finishedAt) })

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
        viewModel.networkDat.getBreakDownTypes().observe(getAct()) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    // TODO: ПО голове себе постучи
                    log.debug("saveBreakDownTypes. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
                else -> log.debug("saveBreakDownTypes:")
            }

        }
    }

    private fun saveFailReason() {
        log.info( "saveFailReason.before")
        viewModel.networkDat.getFailReason().observe(getAct()) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    log.debug("saveFailReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        }
    }

    private fun saveCancelWayReason() {
        log.debug("saveCancelWayReason.before")
        viewModel.networkDat.getCancelWayReason().observe(getAct()) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    log.debug("saveCancelWayReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    log.debug("saveCancelWayReason. Status.ERROR")
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
        log.debug("acceptProgress.before")
        viewModel.networkDat.progress(workOrder.id, ProgressBody(MyUtil.timeStampInSec())).observe(getAct()) { result ->
            resultStatusList.add(result.status)
            getAct().modeSyNChrON_off(false)
            when (result.status) {
                Status.SUCCESS -> {
                    logSentry("acceptProgress Status.SUCCESS ")
                    viewModel.baseDat.setProgressData(workOrder)
                    getAct().modeSyNChrON_off(false)
                    hideProgress()
                }
                else -> {
                    logSentry("acceptProgress Status.ERROR")
                    toast(result.msg)
                    viewModel.baseDat.setNextProcessDate(workOrder)
                }
            }
            if (workOrderSize == resultStatusList.size) {
                onRefreshData()
                hideProgress()
            }
        }

    }

    private fun gotoComplete() {
        gotoSynchronize()
    }


    private fun getNextPlatformToSend(next: (nextSentPlatforms: List<PlatformEntity>, timeBefore: Long) -> Any) {
        var nextSentPlatforms: List<PlatformEntity> = emptyList()
        var timeBeforeInSec: Long = MyUtil.timeStampInSec()
        val lastSynchroTimeInSec = paramS().lastSynchroTimeInSec
        //проблема в секундах синхронизаций
        val m30MinutesInSec = 30 * 60
        if (MyUtil.timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            timeBeforeInSec = lastSynchroTimeInSec + m30MinutesInSec
            nextSentPlatforms = viewModel.baseDat.findPlatforms30min()
            log( "SYNCworkER PLATFORMS IN LAST 30 min")
            next(nextSentPlatforms, timeBeforeInSec)
        }
        if (nextSentPlatforms.isEmpty()) {
            timeBeforeInSec = MyUtil.timeStampInSec()
            nextSentPlatforms = viewModel.baseDat.findLastPlatforms()
            log("SYNCworkER LAST PLATFORMS")
            next(nextSentPlatforms, timeBeforeInSec)
        }

    }

    private fun gotoSynchronize() {
        var lastPlatforms: List<PlatformEntity> = emptyList()
        lastPlatforms = viewModel.baseDat.findLastPlatforms()
        val lastPlatformsSize = lastPlatforms.size

        val deviceId = Settings.Secure.getString(getAct().contentResolver, Settings.Secure.ANDROID_ID)
        if (lastPlatformsSize > 0) {
            showingProgress()
            getNextPlatformToSend() { nextSentPlatforms, timeBeforeInSec ->
                showingProgress("отправляются ${nextSentPlatforms.size} КП\n осталось ${lastPlatformsSize}", true)
                val gps = AppliCation().gps()
                val synchronizeBody = SynchronizeBody(
                    paramS().wayBillId,
                    gps.PointTOBaseData(),
                    deviceId,
                    gps.PointTimeToLastKnowTime_SRV(),
                    nextSentPlatforms)

                viewModel.networkDat.sendLastPlatforms(synchronizeBody).observe(getAct()) { result ->
                    hideProgress()
                    when (result.status) {
                        Status.SUCCESS -> {
                            paramS().lastSynchroTimeInSec = timeBeforeInSec
                            gotoSynchronize()
                        }
                        Status.ERROR -> {
                            toast(result.msg)
                        }
                        Status.NETWORK -> {
                            toast("Проблемы с интернетом")
                        }
                    }
                }
            }

        } else {
            hideProgress()
            completeWorkOrders()
        }
        logSentry("SYNCworkER STARTED")
        log("gotoComplete::synChrONizationDATA:Thread.currentThread().id()=${Thread.currentThread().id}")

    }


    private fun completeWorkOrders() {
        hideInfoDialog()
        navigateMain(R.id.CompleteF)
    }

    private fun showInfoDialog() {
        mInfoDialog?.show()
    }

    //todo:::Гавно кодика?*
    private fun createInfoDialog(next: () -> Any) {
        lateinit var result: AlertDialog
        val builder = AlertDialog.Builder(getAct())
        val inflater = LayoutInflater.from(getAct())
        val view = inflater.inflate(R.layout.f_map__workorder_info, null)
        // TODO:
        val workOrderS = getActualWorkOrderS(true, isFilterMode = false)
//            var infoText = "**Статистика**\n"
        val rvInfo = view.findViewById<RecyclerView>(R.id.rv_f_map__workorder_info)
        mAcbGotoComplete = view.findViewById(R.id.acb_f_map__workorder_info__gotocomplete)
        mAcbGotoComplete?.setOnClickListener {
            viewModel.baseDat.setWorkOrderIsShowForUser(workOrderS)
            gotoComplete()
        }
        setAcbCompleteText(workOrderS)

        rvInfo.layoutManager = LinearLayoutManager(getAct())
        val infoAdapter = InfoAdapter(workOrderS)
        rvInfo.adapter = infoAdapter

        builder.setView(view)
        result = builder.create()
        result.setOnCancelListener {
            val workorder: Unit = viewModel.baseDat.setWorkOrderIsShowForUser(workOrderS)
            next()
            onRefreshData()
        }
        try {
            val window: Window? = result?.window
            val wlp: WindowManager.LayoutParams = window!!.attributes

            wlp.gravity = Gravity.TOP
            //todo:хз что это
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
        } catch (ex: Exception) {
            log.error("createInfoDialog", ex)
        }
        mInfoDialog = result
        mInfoDialog?.show()
    }

    private fun hideInfoDialog() {
        try {
            mInfoDialog?.dismiss()
        } catch (ex: Exception) {
            // TODO: 02.11.2021
            log.error("hideInfoDialog", ex)
        }
    }


    private fun onRefreshBottomBehavior(platforms: List<PlatformEntity>): List<PlatformEntity> {
        // TODO: platforms = getActualPlatformS() :ТОДО
        if (mAdapterBottomBehavior == null) {
            return platforms
        }
        mAdapterBottomBehavior!!.updateItemS(platforms)
        return mAdapterBottomBehavior!!.getItems()
    }

    private fun initBottomBehavior(view: View) {
        val platforms = getActualPlatformS()
        clMapBehavior = view.findViewById(R.id.map_behavior)
        val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)

        bottomSheetBehavior.expandedOffset = 100
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val rvBehavior = view.findViewById<RecyclerView>(R.id.map_behavior_rv)
        mAdapterBottomBehavior = MapActBottomBehaviorAdapter(this, platforms, mWorkOrderFilteredIds)
        rvBehavior.adapter = mAdapterBottomBehavior
//        rvBehavior.adapter.notifyDataSetChanged()
        val llcBottomHavior = view.findViewById<LinearLayoutCompat>(R.id.act_map__bottom_behavior__header)
        llcBottomHavior.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            MyUtil.hideKeyboard(getAct())
        }
        val acetFilterAddress = view.findViewById<AppCompatEditText>(R.id.acet__f_map__bottom_behavior__filter)
        acetFilterAddress.removeTextChangedListener(null)
        acetFilterAddress.addTextChangedListener { newText ->
            mAdapterBottomBehavior?.let { mapBottom ->
                mapBottom.filteredList(newText.toString())
                onRefreshMap(mapBottom.getItems())
            }
        }
        acetFilterAddress.clearFocus()
//        svFilterAddress.setOnQueryTextListener(this)
//        svFilterAddress.setOnSearchClickListener{
//            val diP200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200F, resources.displayMetrics)
//                .toInt()
//            svFilterAddress.layoutParams.width = diP200
//
//            log("svFilterAddress:::setOnSearchClickListener. ")
//        }
//        svFilterAddress.setOnCloseListener{
//            log("svFilterAddress:::setOnCloseListener. before")
//            svFilterAddress.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//            false
//        }
    }

    override fun startPlatformService(item: PlatformEntity) {
        if (AppliCation().gps().isThisPoint(item.coordLat, item.coordLong)) {
            navigateMain(R.id.PhotoBeforeMediaF, item.platformId)
        } else {
            showAlertPlatformByPoint().let { view ->
                val btnOk = view.findViewById<AppCompatButton>(R.id.act_map__dialog_platform_clicked_dtl__alert_by_point__ok)
                btnOk.setOnClickListener {
                    hideDialog()
                    navigateMain(R.id.PhotoBeforeMediaF, item.platformId)
                }
            }
        }

    }

    override fun startPlatformServiceAgain(item: PlatformEntity) {
        hideDialog()
        navigateMain(R.id.PServeF, item.platformId)
    }

    override fun startPlatformProblem(item: PlatformEntity) {
        hideDialog()
        navigateMain(R.id.FAStPhotoFailureMediaF, item.platformId)
    }

    override fun moveCameraPlatform(point: PoinT) {
        mIsAUTOMoveCamera = false
        if (clMapBehavior == null) {
            return
        }
        val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        moveCameraTo(point)
    }

    override fun navigatePlatform(checkPoint: Point) {
        if (drivingModeState) {
            warningClearNavigator(getString(R.string.way_is_exist)).let {
                val btnAccept = it.findViewById<Button>(R.id.accept_btn)
                btnAccept.setOnClickListener {
                    buildNavigator(checkPoint)
                }
            }
        } else {
            buildNavigator(checkPoint)
        }
    }

    override fun openFailureFire(item: PlatformEntity) {
        navigateMain(R.id.FAStPhotoFailureMediaF, item.platformId)
    }

    fun buildNavigator(checkPoint: Point) {
        try {
//            getMapObjCollection().clear()
            clearMapObjectsDrive()
            viewModel.buildMapNavigator(AppliCation().gps(), checkPoint, mDrivingRouter, mDrivingSession)
            drivingModeState = true
            acibNavigatorToggle?.isVisible = drivingModeState
            hideDialog()
            if (clMapBehavior != null) {
                val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            moveCameraTo(AppliCation().gps())
        } catch (ex: Exception) {
            log.error("buildNavigator", ex)
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
//            LocationStatus.NOT_AVAILABLE -> log.debug("GPS STOP")
//            LocationStatus.AVAILABLE -> log.debug("GPS START")
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
        if (mNotifyMap.containsKey(srpId)) {
            return
        }
        mNotifyMap[srpId] = Lnull

        val intent = Intent(getAct(), MainAct::class.java)

        // TODO: !!?R_dos(;:)
        intent.putExtra("srpId", srpId)
        intent.putExtra("platform_id", platformId)
        intent.putExtra("mIsServeAgain", false)

        val pendingIntent = getActivityProperly(getAct(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        AppliCation().showNotificationForce(pendingIntent,
            "Контейнерная площадка №${srpId}",
            "Вы подъехали к контейнерной площадке",
            "Начать обслуживание ",
            srpId,
            NOTIFICATION_CHANNEL_ID__MAP_ACT
        )
    }

    override fun onGetLayout(): Int {
        return R.layout.f_map
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("r_dos//onActivityResult.requestCode=${requestCode}")
        log("r_dos//onActivityResult.resultCode=${resultCode}")

    }
//    override fun onLocationUpdated(location: Location) {
////        log.debug("###################")
//        currentLocation = location
//        LOGWork("onLocationUpdatedaccuracy.${currentLocation.accuracy}")
//        paramS().currentCoordinate = "${location.position.longitude}#${location.position.latitude}"
//        paramS().currentCoordinateAccuracy = location.accuracy.toString()
//        val distanceToPoint = MyUtil.calculateDistance(location.position, selectedPlatformToNavigate)
////        log.debug("Distance: $distanceToPoint")
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
////            log.debug("Distance not arrive")
//        }
//        if (firstTime) {
//            moveCameraToUser(location)
//            firstTime = false
//        }
////        log.debug("Location updated")
//    }

    private fun onRefreshMap(platformS: List<PlatformEntity>) {
        mMapObjectCollection?.clear()
        try {
            mMapObjectCollection = mMapMyYandex.map.mapObjects.addCollection()
        } catch (ex: Exception) {
            log.error("onRefreshMap", ex)
            // TODO: :)!!!
            mMapObjectCollection = null
        }

        mMapObjectCollection?.removeTapListener(this)
        for (platform in platformS) {
            val iconProvider = getIconViewProvider(getAct(), platform)
            val pointYandex = Point(platform.coordLat, platform.coordLong)
            mMapObjectCollection?.addPlacemark(pointYandex, iconProvider)

        }
        mMapObjectCollection?.addTapListener(this)
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeMark = mapObject as PlacemarkMapObject
        val coord = placeMark.geometry
        val plaformS = getActualPlatformS()
        val clickedPlatform = plaformS.find {
            it.coordLat == coord.latitude && it.coordLong == coord.longitude
        }
        if(clickedPlatform == null) {
            toast("Платформа не найдена")
            return false
        }
        log.warn( "onMapObjectTap")
        val platformClickedDtlDialog = MapFPlatformClickedDtlDialog(clickedPlatform, coord, this)
        platformClickedDtlDialog.show(childFragmentManager, "PlaceMarkDetailDialog")

//        todo: !!!R_dos
//        findNavController().navigate(
//            PServeExtendedFragDirections
//            .actionExtendedServeFragmentToContainerServeBottomDialog(item.containerId!!))
        return true
    }


    private fun getIconViewProvider (_context: Context, _platform: PlatformEntity): ViewProvider {
        val result = layoutInflater.inflate(R.layout.map_activity__iconmaker, null)
        val iv = result.findViewById<ImageView>(R.id.map_activity__iconmaker__imageview)
        iv.setImageDrawable(ContextCompat.getDrawable(_context, _platform.getIconFromStatus()))
        val tv = result.findViewById<TextView>(R.id.map_activity__iconmaker__textview)
        tv.isVisible = false
        if (_platform.isOrderTimeWarning()) {
            val orderTime = _platform.getOrderTimeForMaps()
            if (orderTime.isShowForUser()) {
                tv.text = orderTime
                tv.setTextColor(_platform.getOrderTimeColor(getAct()))
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


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        log.error( "r_dos/onResume.before")
        onRefreshData()
        log.error( "r_dos/onResume.after")
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.f_map__workorder_info__rv_item, parent, false)
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
            }

        }

        fun getItemS(): List<WorkOrderEntity> {
            return p_workOrderS
        }

        inner class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvPlatformCnt: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__platform_cnt)
            }
            val tvPlatformSuccess: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__platform_success)
            }
            val tvPlatformError: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__platform_error)
            }
            val tvPlatformProgress: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__platform_progress)
            }

            val tvContainerCnt: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__container_cnt)
            }
            val tvContainerSuccess: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__container_success)
            }
            val tvContainerError: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__container_error)
            }
            val tvContainerProgress: TextView by lazy {
                itemView.findViewById(R.id.tv_f_map__workorder_info__rv__container_progress)
            }
            val accbCheckBox: AppCompatCheckBox by lazy {
                val checkBox = itemView.findViewById<AppCompatCheckBox>(R.id.act_map__workorder_info__checkbox)
                checkBox
            }
        }
    }

    private fun setAcbCompleteText(workOrderS: List<WorkOrderEntity>) {
        val cntWorkOrderShowForUser = workOrderS.filter { it.isShowForUser }.size

        if (cntWorkOrderShowForUser <= 0) {
            mAcbGotoComplete?.isEnabled = false
            mAcbGotoComplete?.text = "Выберите задание"
            mAcbGotoComplete?.background = ContextCompat.getDrawable(getAct(), R.drawable.bg_button_gray)
            return
        }
        if (getActualWorkOrderS().size == 1) {
            mAcbGotoComplete?.text = "Завершить задание"
        } else {
            mAcbGotoComplete?.text = "Завершить задания"
        }
        mAcbGotoComplete?.background = ContextCompat.getDrawable(getAct(), R.drawable.bg_button)
        mAcbGotoComplete?.isEnabled = true
    }

    /** ********************************************************************************************
     * ЗДЕСЬ интерфейсы interface*/

    override fun onStart(p0: Map, p1: CameraPosition) {
        mIsAUTOMoveCamera = false
    }

    override fun onCancel(p0: Map, p1: CameraPosition) {
        this as InertiaMoveListener
        log.debug("onCancel")
    }

    override fun onFinish(p0: Map, p1: CameraPosition) {
        log.debug("onFinish")
//        this as InertiaMoveListener
    }
}
