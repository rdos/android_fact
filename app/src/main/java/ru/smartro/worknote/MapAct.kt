package ru.smartro.worknote

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
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
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import kotlinx.android.synthetic.main.dialog_early_complete.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.abs.ActAbstract
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.awORKOLDs.base.BaseViewModel
import ru.smartro.worknote.awORKOLDs.extensions.*
import ru.smartro.worknote.awORKOLDs.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.ProgressBody
import ru.smartro.worknote.awORKOLDs.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.awORKOLDs.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.work.MapActBottomBehaviorAdapter
import ru.smartro.worknote.work.MapActPlatformClickedDtlDialog
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.WorkOrderEntity
import ru.smartro.worknote.work.ac.PERMISSIONS
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.platform_serve.PlatformServeAct
import ru.smartro.worknote.work.ui.DebugAct
import ru.smartro.worknote.work.ui.JournalChatAct
import ru.smartro.worknote.work.ui.PlatformFailureAct
import java.util.*
import kotlin.math.round


//todo:R_dos)!!
//todo: тодо:!r_dos
//Двигается карта deselectGeoObject()
//todo:FOR-_dos val hasNotServedPlatform = platforms.any { found -> found.status == StatusEnum.NEW }
// TODO:r_dos! а то checked, Mem РАЗ БОР ПО ЛЁТОВ будет тутРАЗ БОР ПО ЛЁТОВ будет тутРАЗ БОР ПО ЛЁТОВ будет тут
//AppPreferences.isHasTask = false
//override fun onResume() {  mPlatforms = vs.findPlatfor

// бот getNetDATEsetBaseDate вот таких бы(ой, касты) нам меньше(или НА код  Г а ВСЕ ) , жена как и супруга в доле, но gam(e)_версия а не тип игры 3)1_2
//AppPreferences.wayBillId AppPreferences.organisationId
//AppPreferences.wayBillNumber  AppPreferences.vehicleId
// TODO: I 12.11.1997 import имя getNetDATEsetBaseDate и там где рядом netDat и SrvDate а где смысл как в python
/** 29.06.  -2+2001+3   05.11))*/
class MapAct : ActAbstract(),
    /*UserLocationObjectListener,*/
    MapActBottomBehaviorAdapter.PlatformClickListener, MapObjectTapListener, UserLocationObjectListener,  InertiaMoveListener {
//    private lateinit var mMapObjectCollection: MapObjectCollection
    private var mIsAUTOMoveCamera: Boolean = false
    private var mInfoDialog: AlertDialog? = null
    private lateinit var mAcbInfo: AppCompatButton
    private lateinit var mAcbComplete: AppCompatButton
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

        val platformNear = vs.baseDat.findPlatformByCoord(point, point.getAccuracy())

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
        val isWorkOrdersInProgress = vs.baseDat.hasWorkOrderInProgress_know0()
        if (!isWorkOrdersInProgress) {
            showingProgress()
            val extraPramId = getPutExtraParam_ID()
            val workOrderS = getWorkOrders(extraPramId)
            getNetDATEsetBaseDate(workOrderS)
        }

        mMapMyYandex.map.addInertiaMoveListener(this)
        mAcbInfo = findViewById(R.id.acb_act_map__info)

        mAcbInfo.setOnClickListener {
           gotoInfoDialog()
        }

        setInfoData()

        initMapView()
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
            clearMapIbjectsDrive()
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

        AppliCation().startWorkER()
        AppliCation().startLocationService()

        //todo:  R_dos!!! modeSyNChrON_off(false)
        paramS.isModeSYNChrONize = true
//        setDevelMode()
    }


    private fun gotoInfoDialog() {
        showInfoDialog().let {
            //O!
            mAcbComplete = it.findViewById(R.id.acb_act_map__workorder_info__complete)
            mAcbComplete.isEnabled = false
            //Oo!!
            initWorkOrderInfo(it)
            mAcbComplete.setOnClickListener{
                gotoComplete()
            }
//            var infoText = "**Статистика**\n"
            val rvInfo = it.findViewById<RecyclerView>(R.id.rv_act_map__workorder_info)
            rvInfo.layoutManager = LinearLayoutManager(this)
            rvInfo.adapter = InfoAdapter(getWorkOrders())
        }
    }

    private var mMapObjectsDrive: MapObjectCollection? = null

    private fun clearMapIbjectsDrive() {
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
        mWorkOrderS = vs.baseDat.findWorkOrders()
        mPlatformS = vs.baseDat.findPlatforms()

//        procedure1(mPlatformS!!)

        initMapView()
        initBottomBehavior()
        setInfoData()
    }


    private fun setInfoData() {
        val workOrders = getWorkOrders()
        var platformCnt = 0
        var platformProgress = 0
        for(workOrder in workOrders) {
            platformCnt += workOrder.cnt_platform
            platformProgress += workOrder.cnt_platform - workOrder.cntPlatformProgress()
        }
        mAcbInfo.text = "$platformProgress / $platformCnt"
    }

    private fun getWorkOrders(extraPramId: Int? = null): List<WorkOrderEntity> {
        if (mWorkOrderS == null) {
            Log.d(TAG, "r_dos/getWorkOrders.before")
            mWorkOrderS = vs.baseDat.findWorkOrders(extraPramId)
            Log.i(TAG, "r_dos/getWorkOrders.after")
        }
        return mWorkOrderS!!
    }

    private fun getActualPlatforms(isForceGetBaseData: Boolean = false): List<PlatformEntity> {
        if (mPlatformS == null || isForceGetBaseData) {
            mPlatformS = vs.baseDat.findPlatforms()
        }

        return mPlatformS!!
    }


    //TODO:r_dos ну вот!.. ага... теперь с понтами все в порядке будет... OS
    private fun getNetDATEsetBaseDate(workOrderS: List<WorkOrderEntity>) {
        saveFailReason()
        saveCancelWayReason()
        saveBreakDownTypes()
//                    val hand = Handler(Looper.getMainLooper())
        for (workOrder in workOrderS) {
            logSentry(workOrder.id.toString())
            progressNetData(workOrder)
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
    private fun progressNetData(woRKoRDeRknow1: WorkOrderEntity) {
        Log.d(TAG, "acceptProgress.before")
        vs.networkDat.progress(woRKoRDeRknow1.id, ProgressBody(MyUtil.timeStamp())).observe(this) { result ->
            resultStatusList.add(result.status)
            modeSyNChrON_off(false)
            when (result.status) {
                Status.SUCCESS -> {
                    logSentry("acceptProgress Status.SUCCESS ")
//                        AppPreferences.isHasTask = true
                    vs.baseDat.setProgressData(woRKoRDeRknow1)
                    modeSyNChrON_off(false)
                    onRefreshData()
                    hideProgress()
                }
                else -> {
                    logSentry("acceptProgress Status.ERROR")
                    toast(result.msg)
//                        AppPreferences.isHasTask = false
                    vs.baseDat.setNextProcessDate(woRKoRDeRknow1)
//                    break
                }
            }
            if (getWorkOrders().size <= resultStatusList.size) {
                hideProgress()
            }
        }

    }


    private fun successCompleteWayBill(workOrder: WorkOrderEntity) {
        val totalVolume =  vs.baseDat.findContainersVolume(workOrder.id)
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
                    showingProgress()
                    vs.networkDat.completeWay(workOrder.id, body)
                        .observe(this@MapAct) { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    hideProgress()
                                    hideInfoDialog()
                                    vs.baseDat.setCompleteData(workOrder)
                                    if (vs.baseDat.hasNotWorkOrderInProgress()) {
                                        finishTask(this@MapAct)
                                    } else {
                                        onRefreshData()
                                    }
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
                } else {
                    toast("Выберите тип показателей")
                }
            }
        }
    }

    private fun earlyCompleteWorkOrder(workOrder: WorkOrderEntity) {
        val cancelWayReasonS = vs.baseDat.findCancelWayReason()
        showDialogEarlyComplete(cancelWayReasonS, workOrder.id, workOrder.name).let { view ->
            val totalVolume = vs.baseDat.findContainersVolume(workOrder.id)
            view.unload_value_et.setText("$totalVolume")
            view.accept_btn.setOnClickListener {
                if (!view.reason_et.text.isNullOrEmpty() &&
                    (view.early_volume_tg.isChecked || view.early_weight_tg.isChecked)
                    && !view.unload_value_et.text.isNullOrEmpty()
                ) {
                    val failureId = vs.findCancelWayReasonByValue(view.reason_et.text.toString())
                    val unloadValue = round(
                        view.unload_value_et.text.toString().toDouble() * 100
                    ) / 100
                    val unloadType = if (view.early_volume_tg.isChecked) 1 else 2
                    val body = EarlyCompleteBody(failureId, MyUtil.timeStamp(), unloadType, unloadValue)
                    showingProgress()

                    vs.networkDat.earlyComplete(workOrder.id, body)
                        .observe(this@MapAct) { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    hideProgress()
                                    hideDialog()
                                    hideInfoDialog()
                                    vs.baseDat.setCompleteData(workOrder)
                                    if (vs.baseDat.hasNotWorkOrderInProgress()) {
                                        finishTask(this)
                                    } else {
                                        onRefreshData()
                                    }
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
                } else {
                    toast("Заполните все поля")
                }
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
        val checkBoxList = getWorkOrderInfoCheckBoxs()
        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                val workOrders = getWorkOrders()
                val workOrderId = checkBox.tag as Int
                val workOrder = workOrders.find { found -> found.id == workOrderId }
                if (workOrder!!.cnt_platform_status_new <= 0) {
                    successCompleteWayBill(workOrder)
                } else {
                    earlyCompleteWorkOrder(workOrder)
                }
            }

        }
    }

    private fun showInfoDialog(): View {
//        val dlg = AlertDialog.Builder(this, R.style.Theme_Inventory_Dialog)
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.act_map__workorder_info, null)
        builder.setView(view)
        mInfoDialog = builder.create()
        try {
            val window: Window? = mInfoDialog?.window
            val wlp: WindowManager.LayoutParams = window!!.attributes

            wlp.gravity = Gravity.TOP
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            mInfoDialog?.show()
        } catch (ex: Exception) {
            Log.e(TAG, "showInfoDialog", ex)
        }
        return view
    }

    private fun hideInfoDialog() {
        try {
            mInfoDialog?.dismiss()
        } catch (ex: Exception) {
            // TODO: 02.11.2021
            Log.e(TAG, "hideInfoDialog", ex)
        }
    }


    private fun initWorkOrderInfo(view: View) {
        val llcInfo = view.findViewById<LinearLayoutCompat>(R.id.llc_act_map__workorder_info)
        val inflater = LayoutInflater.from(this)
        val workOrders = getWorkOrders()
        mCheckBoxList.clear()
        for (workOrder in workOrders) {
            val apcbComplete = inflater.inflate(R.layout.act_map__workorder_info__checkbox, llcInfo, false) as AppCompatCheckBox
            apcbComplete.text = "${workOrder.id} ${workOrder.name}"
            apcbComplete.tag = workOrder.id
            mCheckBoxList.add(apcbComplete)
            llcInfo.addView(apcbComplete)
            apcbComplete.setOnCheckedChangeListener { compoundButton, b ->
                acbCompleteIsEnable()
            }
            apcbComplete.isChecked = true
//            if (workOrders.size == 1) {
//                apcbComplete.isChecked = true
//            }
//            apcbComplete.setOnCheckedChangeListener { buttonView, isChecked ->
//                if (isChecked) {
//                    mFilteredWayTaskIds.add(wayTask.id!!)
//                } else {
//                    mFilteredWayTaskIds.remove(wayTask.id!!)
//                }
//                initMapView(true)
//                initBottomBehavior()
//            }
        }
    }


    private val mCheckBoxList = mutableListOf<AppCompatCheckBox>()
    private fun getWorkOrderInfoCheckBoxs(): List<AppCompatCheckBox> {
        return mCheckBoxList
    }

    private fun acbCompleteIsEnable() {
        val checkBoxList = getWorkOrderInfoCheckBoxs()
        mAcbComplete.isEnabled = false
        mAcbComplete.text = "Выберите задание"
        mAcbComplete.background = ContextCompat.getDrawable(this, R.drawable.bg_button_gray)
        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                mAcbComplete.text = "Завершить маршрут"
                mAcbComplete.background = ContextCompat.getDrawable(this, R.drawable.bg_button)
                mAcbComplete.isEnabled = true
                return
            }
        }
    }


    private fun initBottomBehavior() {
        val platforms = getActualPlatforms()
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)

        bottomSheetBehavior.expandedOffset = 100
        val rvBehavior = findViewById<RecyclerView>(R.id.map_behavior_rv)
        val adapterBottomBehavior = MapActBottomBehaviorAdapter(this, platforms, mWorkOrderFilteredIds)
        rvBehavior.adapter = adapterBottomBehavior

        act_map__bottom_behavior__header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val acbGotoComplete = findViewById<AppCompatButton>(R.id.acb_act_map__bottom_behavior__gotocomplete)
        if (getWorkOrders().size <= 1) {
            acbGotoComplete.text = "Завершить маршрут"
        } else {
            acbGotoComplete.text ="К завершению маршрута"
        }
        acbGotoComplete.setOnClickListener{

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            gotoInfoDialog()
            if (getWorkOrders().size <= 1) {
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
            clearMapIbjectsDrive()
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
        mNotifyMap[srpId] = -1
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

    private fun initMapView() {
        val platforms = getActualPlatforms()
        Log.d("initMapView : platforms :::", "${platforms.joinToString { el -> "{" + "id: ${el.platformId}, progress: ${el.isWorkOrderProgress}, complete: ${el.isWorkOrderComplete}" + "}" }}")
        val mMapObjectCollection = mMapMyYandex.map.mapObjects
        mMapObjectCollection.removeTapListener(this)
        mMapObjectCollection.clear()
        addPlaceMarks(this, mMapObjectCollection, platforms)
        mMapObjectCollection.addTapListener(this)
    }

    private fun addPlaceMarks(context: Context, mapObjectCollection: MapObjectCollection, platforms: List<PlatformEntity>) {
        for(platform in platforms) {
            val iconProvider = getIconViewProvider(context, platform)
            val pointYandex = Point(platform.coords[0]!!, platform.coords[1]!!)
            mapObjectCollection.addPlacemark(pointYandex, iconProvider)
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

    fun finishTask(context: AppCompatActivity) {
        Log.i(TAG, "finishTask")
        modeSyNChrON_off()
        vs.baseDat.clearDataBase()
//            AppPreferences.isHasTask = false
        context.showSuccessComplete().let {
            it.finish_accept_btn.setOnClickListener {
                context.startActivity(Intent(context, StartAct::class.java))
                context.finish()
            }
            it.exit_btn.setOnClickListener {
                MyUtil.logout(context)
            }
        }
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
            return baseDat.findCancelWayReason()
        }

        fun findCancelWayReasonByValue(reason: String): Int {
            return baseDat.findCancelWayReasonByValue(reason)
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
    //                info//          //            for(workOrder in workOrders) {  }Text += "\n${workOrder.id} ${workOrder.name}________"

    inner class InfoAdapter(private val workOrderS: List<WorkOrderEntity>) :
        RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.act_map__workorder_info__rv_item, parent, false)
//            logSentry("BB")
            return InfoViewHolder(view)
        }

        override fun getItemCount(): Int {
            return workOrderS.size
        }

        override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
            val workOrder = workOrderS[position]

            holder.tvName.text = workOrder.name
            holder.tvPlatformCnt.text = "Площадки - ${workOrder.cnt_platform}"
            holder.tvPlatformSuccess.text = workOrder.cnt_platform_status_success.toString()
            holder.tvPlatformError.text = workOrder.cnt_platform_status_error.toString()
            holder.tvPlatformProgress.text = workOrder.cntPlatformProgress().toString()

            holder.tvContainerCnt.text = "Контейнеры - ${workOrder.cnt_container}"
            holder.tvContainerSuccess.text = workOrder.cnt_container_status_success.toString()
            holder.tvContainerError.text = workOrder.cnt_container_status_error.toString()
            holder.tvContainerProgress.text = workOrder.cntContainerProgress().toString()
        }

        inner class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView by lazy {
                itemView.findViewById(R.id.tv_act_map__workorder_info__rv__name)
            }
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
        }
    }




    override fun onStart(p0: Map, p1: CameraPosition) {
//        this as InertiaMoveListener
        mIsAUTOMoveCamera = false
    }

    override fun onCancel(p0: Map, p1: CameraPosition) {
        this as InertiaMoveListener
//        Log.d("AAAA", "onCancel")
    }

    override fun onFinish(p0: Map, p1: CameraPosition) {
//        Log.d("AAAA", "onFinish")
//        this as InertiaMoveListener
    }

}



//                infoText += "\nПлощадки:   всего ${workOrder.cnt_platform}"
//                infoText += "\nобслуженно/осталось/невывоз:\n"
//                infoText += "${workOrder.cnt_platform_status_success}"
//                infoText += "/${workOrder.cntPlatformProgress()}"
//                infoText += "/${workOrder.cnt_platform_status_error}"
//                infoText += "\nКонтейнеры:   всего ${workOrder.cnt_container}"
//                infoText += "\nобслуженно/осталось/невывоз:\n"
//                infoText += "${workOrder.cnt_container_status_success}"
//                infoText += "/${workOrder.cntContainerProgress()}"
//                infoText += "/${workOrder.cnt_container_status_error}\n"



//    private fun procedure1(platformS: List<PlatformEntity>) {
//        var minLat: Double = platformS[0].coords[0]!!
//        var maxLat: Double = platformS[0].coords[0]!!
//
//        var minLong: Double = platformS[0].coords[1]!!
//        var maxLong: Double = platformS[0].coords[1]!!
//
//        val mapCoordinate = emptyMap<Double?, Double?>().toMutableMap()
//
//        for(platform in platformS) {
//            LOGWork("lat(long)=${platform.coords[0]}(${platform.coords[1]})")
//            vs.baseDat.updateFailureComment(platform.platformId!!, "")
//            if (minLat > platform.coords[0]!!) {
//                minLat = platform.coords[0]!!
//            }
//            if (minLong > platform.coords[1]!!) {
//                minLong = platform.coords[1]!!
//            }
//            if (maxLat < platform.coords[0]!!) {
//                maxLat = platform.coords[0]!!
//            }
//            if (maxLong < platform.coords[1]!!) {
//                maxLong = platform.coords[1]!!
//            }
//            mapCoordinate[platform.coords[0]] = platform.coords[1]
//        }
//        LOGWork("tit.minLat=${minLat}")
//        LOGWork("tit.maxLat=${maxLat}")
//        LOGWork("tit.minLong=${minLong}")
//        LOGWork("tit.maxLong=${maxLong}")
//
//        val regionCnt = 10
//        val stepLat = (maxLat - minLat) / regionCnt
//        val stepLong = (maxLong - minLong) / regionCnt
//
//        var regionId = 0
//        var regionStartLat = minLat
//        var regionEndLat = minLat
//        var regionStartLong = minLong
//        var regionEndLong = minLong
//
//        for (idx in 1..regionCnt){
//            if (idx < regionCnt) {
//                regionEndLat = regionStartLat + stepLat
//            } else {
//                regionEndLat = maxLat
//            }
//
//
//            regionStartLong = minLong
//            for(jdx in 1..regionCnt) {
//                regionId++
//                LOGWork("tit.regionIdregionId=${regionId}")
//                LOGWork("tit.regionStartLat=${regionStartLat})")
//                LOGWork("tit.regionEndLat=${regionEndLat})")
//                if (jdx < regionCnt) {
//                    regionEndLong = regionStartLong + stepLong
//                } else {
//                    regionEndLong = maxLong
//                }
//                LOGWork("tit.regionStartLong=${regionStartLong})")
//                LOGWork("tit.regionEndLong=${regionEndLong})")
//                for(platform in platformS) {
//                    if (platform.address == "Ульяновская область, Мелекесский район, Лесной, Дорожная,9") {
//                        LOGWork("tit.=)")
//                    }
//                    val coordLat = platform.coords[0]!!
//                    val coordLong = platform.coords[1]!!
//                    val lll = (regionEndLat - regionStartLat) * 0.2
//                    val sss = (regionEndLong - regionStartLong) * 0.2
//                    if ((coordLat in regionStartLat-lll..regionEndLat+lll) && (coordLong in regionStartLong-sss..regionEndLong+sss)) {
//                        vs.baseDat.addFailureComment(platform.platformId!!, regionId.toString())
//                        LOGWork("tit.regionId=${regionId} for ${coordLat}(${coordLong})")
//                    }
//                }
//                regionStartLong = regionEndLong
//            }
//            regionStartLat = regionEndLat
//        }
//
//        LOGWork("tit.stepLat=${stepLat}")
//        LOGWork("tit.stepLong=${stepLong}")
//
//        val sortList = mapCoordinate.toList().sortedBy { (_, value) -> value }
//        LOGWork("sortList=${sortList[0].first}")
//    }