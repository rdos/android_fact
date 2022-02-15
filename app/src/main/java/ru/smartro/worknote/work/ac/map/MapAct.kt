package ru.smartro.worknote.work.ac.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.Error
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.act_map.*
import kotlinx.android.synthetic.main.act_map__bottom_behavior.*
import kotlinx.android.synthetic.main.alert_failure_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.*
import kotlinx.android.synthetic.main.alert_finish_way.view.accept_btn
import kotlinx.android.synthetic.main.alert_successful_complete.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.base.AbstractAct
import ru.smartro.worknote.base.BaseViewModel
import ru.smartro.worknote.extensions.*
import ru.smartro.worknote.isShowForUser
import ru.smartro.worknote.work.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.ui.debug.DebugActivity
import ru.smartro.worknote.ui.journal.JournalAct
import ru.smartro.worknote.work.platform_serve.PlatformServeAct
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.SynchronizeWorker
import ru.smartro.worknote.work.WorkOrderEntity
import ru.smartro.worknote.work.ac.checklist.StartWayBillAct
import java.util.concurrent.TimeUnit
import kotlin.math.round


//todo:FOR-_dos val hasNotServedPlatform = platforms.any { found -> found.status == StatusEnum.NEW }
// TODO:r_dos! а то checked, Mem РАЗ БОР ПО ЛЁТОВ будет тутРАЗ БОР ПО ЛЁТОВ будет тутРАЗ БОР ПО ЛЁТОВ будет тут
//AppPreferences.isHasTask = false
//override fun onResume() {  mPlatforms = vs.findPlatfor
// бот getNetDATEsetBaseDate вот таких бы(ой, касты) нам меньше(или НА код  Г а ВСЕ ) , жена как и супруга в доле, но gam(e)_версия а не тип игры 3)1_2
//AppPreferences.wayBillId AppPreferences.organisationId
//AppPreferences.wayBillNumber  AppPreferences.vehicleId
// TODO: I 12.11.1997 import имя getNetDATEsetBaseDate и там где рядом netDat и SrvDate а где смысл как в python
class MapAct : AbstractAct(),
    /*UserLocationObjectListener,*/
    PlatformAdapter.PlatformClickListener, LocationListener, MapObjectTapListener {
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
    private var firstTime = true
    private var isOnPointFirstTime = true

    private val locationListener = this as LocationListener
    private val MIN_METERS = 50

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mDrivingRouter: DrivingRouter
    private lateinit var mDrivingSession: DrivingSession.DrivingRouteListener
    private lateinit var currentLocation: Location
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var selectedPlatformToNavigate: Point
    private lateinit var locationManager: LocationManager
    private lateinit var mapKit: MapKit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.act_map)

        mMapMyYandex = findViewById(R.id.map_view)
        val isWorkOrdersInProgress = vs.baseDat.hasWorkOrderInProgress_know0()
        if (!isWorkOrdersInProgress) {
            showingProgress()
            val extraPramId = getPutExtraParam_ID()
            val workOrderS = getWorkOrders(extraPramId)
            getNetDATEsetBaseDate(workOrderS)
        }
        mAcbInfo = findViewById<AppCompatButton>(R.id.acb_act_map__info)

        mAcbInfo.setOnClickListener {
            showInfoDialog().let {
                //O!
                mAcbComplete = it.findViewById(R.id.acb_act_map__workoder_info__complete)
                mAcbComplete.isEnabled = false
                //Oo!!
                initWorkOrderInfo(it)
                mAcbComplete.setOnClickListener{
                    gotoComplete()
                }
                val actvInfo = it.findViewById<AppCompatTextView>(R.id.actv_act_map__workoder_info)

                val workOrders = getWorkOrders()
                var infoText = "Статистика\n_______"
                for(workOrder in workOrders) {
                    infoText += "\n${workOrder.id} ${workOrder.name}________"
                    infoText += "\nПлощадки:   всего ${workOrder.cnt_platform}"
                    infoText += "\nобслуженно/осталось/невывоз:\n"
                    infoText += "${workOrder.cnt_platform_status_success}"
                    infoText += "/${workOrder.cntPlatformProgress()}"
                    infoText += "/${workOrder.cnt_platform_status_error}"
                    infoText += "\nКонтейнеры:   всего ${workOrder.cnt_container}"
                    infoText += "\nобслуженно/осталось/невывоз:\n"
                    infoText += "${workOrder.cnt_container_status_success}"
                    infoText += "/${workOrder.cntContainerProgress()}"
                    infoText += "/${workOrder.cnt_container_status_error}\n"
                }

                actvInfo.text = infoText
            }
        }

        setInfoData()

        initSynchronizeWorker()
        initMapView()

        initBottomBehavior()
        initUserLocation()

        // TODO: 21.10.2021 r_dos 
//        map_toast.setOnClickListener{
//            toast("${AppPreferences.wayBillId}")
//        }


        navigator_toggle_fab.setOnClickListener {
            drivingModeState = false
            navigator_toggle_fab.isVisible = drivingModeState
            clearMapIbjectsDrive()
            hideDialog()
        }

        log_fab.setOnClickListener {
            startActivity(Intent(this@MapAct, JournalAct::class.java))
        }

        selectedPlatformToNavigate = Point(0.0, 0.0)
        mDrivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mDrivingSession = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutesError(p0: Error) {
                toast("Ошибка при построении маршрута")
            }

            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {

                routes.forEach { getMapObjectsDrive()?.addPolyline(it.geometry) }
            }

        }

        setDevelMode()
    }

    private var mMapObjectsDrive: MapObjectCollection? = null

    private fun clearMapIbjectsDrive() {
        mMapObjectsDrive?.clear()
        mMapObjectsDrive = null
    }

    private fun getMapObjectsDrive(): MapObjectCollection? {
        if (mMapObjectsDrive == null) {
            mMapObjectsDrive = getMapObjCollection().addCollection()
        }
        return mMapObjectsDrive
    }

    private fun setDevelMode() {
        if (isDevelMode) {
            mAcbInfo.setOnLongClickListener {
                //ВОТ тут прошло 3 или больше секунды с начала нажатия
                //можно что-то запустить
                WorkManager.getInstance(this@MapAct).cancelUniqueWork("UploadData")
                vs.clearData()
                val intent = Intent(this, StartWayBillAct::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(intent)
                AppPreferences.isDevelMode()
                return@setOnLongClickListener true
            }

//            mAcbInfo.setOnTouchListener(object : View.OnTouchListener {
//                var startTime: Long = 0
//                override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> startTime = System.currentTimeMillis()
//                        MotionEvent.ACTION_MOVE -> {}
//                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                            val totalTime: Long = System.currentTimeMillis() - startTime
//                            val totalSecunds = totalTime / 1000
//                            if (totalSecunds >= 5) {
//                                //ВОТ тут прошло 3 или больше секунды с начала нажатия
//                                //можно что-то запустить
//                                WorkManager.getInstance(this@MapAct).cancelUniqueWork("UploadData")
//                                vs.clearData()
//                                MyUtil.logout(this@MapAct)
//                            }
//                        }
//                    }
//                    return true
//                }
//            })
        }
    }

    private fun refreshData() {
        mWorkOrderS = vs.baseDat.findWorkOrders()
        mPlatformS = vs.baseDat.findPlatforms()
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
            platformProgress += workOrder.cntPlatformProgress()!!
        }
        mAcbInfo.text = "${platformProgress} / ${platformCnt}"
    }

    private fun getWorkOrders(extraPramId: Int? = null): List<WorkOrderEntity> {
        if (mWorkOrderS == null) {
            mWorkOrderS = vs.baseDat.findWorkOrders(extraPramId)
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
        vs.networkDat.getBreakDownTypes().observe(this, Observer { result ->
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

        })
    }

    private fun saveFailReason() {
        Log.i(TAG, "saveFailReason.before")
        vs.networkDat.getFailReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveFailReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    toast(result.msg)
                }
            }
        })
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun saveCancelWayReason() {
        Log.d(TAG, "saveCancelWayReason.before")
        vs.networkDat.getCancelWayReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveCancelWayReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    Log.d(TAG, "saveCancelWayReason. Status.ERROR")
                    toast(result.msg)
                }
                else -> { oops()}
            }
        })
    }

    val resultStatusList = mutableListOf<Status>()
    private fun progressNetData(woRKoRDeRknow1: WorkOrderEntity) {
        Log.d(TAG, "acceptProgress.before")
        vs.networkDat.progress(woRKoRDeRknow1.id, ProgressBody(MyUtil.timeStamp())).observe(this, Observer { result ->
            resultStatusList.add(result.status)
            when (result.status) {
                Status.SUCCESS -> {
                    logSentry("acceptProgress Status.SUCCESS ")
//                        AppPreferences.isHasTask = true
                    vs.baseDat.setProgressData(woRKoRDeRknow1)
                    refreshData()
                    hideProgress()
                }
                else -> {
                    logSentry( "acceptProgress Status.ERROR")
                    toast(result.msg)
//                        AppPreferences.isHasTask = false
                    vs.baseDat.setNextProcessDate(woRKoRDeRknow1)
//                    break
                }
            }
            if(getWorkOrders().size <= resultStatusList.size) {
                hideProgress()
            }
        })

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
                        .observe(this@MapAct, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    hideProgress()
                                    hideInfoDialog()
                                    vs.baseDat.setCompleteData(workOrder)
                                    if (vs.baseDat.hasNotWorkOrderInProgress()) {
                                        vs.finishTask(this@MapAct)
                                    } else {
                                        refreshData()
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
                        })
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
                        .observe(this@MapAct, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    hideProgress()
                                    hideDialog()
                                    hideInfoDialog()
                                    vs.baseDat.setCompleteData(workOrder)
                                    if (vs.baseDat.hasNotWorkOrderInProgress()) {
                                        vs.finishTask(this)
                                    }else {
                                        refreshData()
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
        var lat = 0.0
        var long = 0.0
        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val currentCoordinate = AppPreferences.currentCoordinate
        if (currentCoordinate.contains("#")) {
            long = currentCoordinate.substringAfter("#").toDouble()
            lat = currentCoordinate.substringBefore("#").toDouble()
        }
        val synchronizeBody = SynchronizeBody(
            AppPreferences.wayBillId, listOf(lat, long),
            deviceId, lastPlatforms)

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
                if (workOrder!!.cnt_container_status_new <= 0) {
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
            if (workOrders.size == 1) {
                apcbComplete.isChecked = true
            }
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
        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                mAcbComplete.isEnabled = true
                return
            }
        }
    }


    private fun getMapObjCollection(): MapObjectCollection {
        val res = mMapMyYandex.map.mapObjects
        return res
    }



    private fun initSynchronizeWorker() {
        Log.w(TAG, "initSynchronizeWorker.before thread_id=${Thread.currentThread().id}")
        AppPreferences.workerStatus = true
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
        Log.d(TAG, "initSynchronizeWorker.after")
    }


    private fun initBottomBehavior() {
        val platforms = getActualPlatforms()
        bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        map_behavior_rv.adapter = PlatformAdapter(this, platforms, mWorkOrderFilteredIds)

        act_map__bottom_behavior__header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
            }



    override fun startPlatformService(item: PlatformEntity) {
        val intent = Intent(this, PlatformServeAct::class.java)
        intent.putExtra("platform_id", item.platformId)
        startActivity(intent)
    }

    override fun startPlatformProblem(item: PlatformEntity) {
        hideDialog()
        val intent = Intent(this, ExtremeProblemActivity::class.java)
        intent.putExtra("platform_id", item.platformId)
        startActivityForResult(intent, REQUEST_EXIT)
    }

    override fun moveCameraPlatform(point: Point) {
        val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        mMapMyYandex.map.move(CameraPosition(point, 16.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
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
            selectedPlatformToNavigate = checkPoint
            vs.buildMapNavigator(currentLocation, checkPoint, mDrivingRouter, mDrivingSession)
            drivingModeState = true
            navigator_toggle_fab.isVisible = drivingModeState
            hideDialog()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            moveCameraToUser(currentLocation)
        } catch (ex: Exception) {
            Log.e(TAG, "buildNavigator", ex)
            toast(getString(R.string.error_build_way))
        }

    }

    private fun moveCameraToUser(location: Location) {
        mMapMyYandex.map.move(
            CameraPosition(location.position, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F), null
        )
    }


    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
        when (locationStatus) {
            LocationStatus.NOT_AVAILABLE -> Log.d("LogDistance", "GPS STOP")
            LocationStatus.AVAILABLE -> Log.d("LogDistance", "GPS START")

        }
    }

    private fun initMapView() {
        val platforms = getActualPlatforms()
//        mapsMyYandex.map.mapObjects.removeTapListener(this)
        val mapObjectCollection = getMapObjCollection()
//        mapObjectCollection.clear()
        mapObjectCollection.removeTapListener(this)
        addPlaceMarks(this, mapObjectCollection, platforms)
        mapObjectCollection.addTapListener(this)
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeMark = mapObject as PlacemarkMapObject
        val coordinate = placeMark.geometry
        val clickedPlatform = vs.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
        val platformClickedDtlDialog = PlatformClickedDtlDialog(clickedPlatform, coordinate)
        platformClickedDtlDialog.show(supportFragmentManager, "PlaceMarkDetailDialog")
        return true
    }

    private fun getIconViewProvider(_context: Context, _platform: PlatformEntity): ViewProvider {
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
//    override fun onObjectAdded(userLocationView: UserLocationView) {
//        val pinIcon = userLocationView.arrow.useCompositeIcon()
//        pinIcon.setIcon(
//            "icon",
//            ImageProvider.fromResource(this, R.drawable.ic_truck_icon),
//            IconStyle().setAnchor(PointF(0.5f, 1f))
//                .setRotationType(RotationType.ROTATE)
//                .setZIndex(0f)
//        )
//
//        userLocationView.accuracyCircle.isVisible = false
//        userLocationLayer.setObjectListener(null)
//    }

//    override fun onObjectRemoved(p0: UserLocationView) {
//
//    }
//
//    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
//
//    }

    private fun addPlaceMarks(context: Context, mapObjectCollection: MapObjectCollection, platforms: List<PlatformEntity>) {
        for(platform in platforms) {
            val iconProvider = getIconViewProvider(context, platform)
            val pointYandex = Point(platform.coords[0]!!, platform.coords[1]!!)
            mapObjectCollection.addPlacemark(pointYandex, iconProvider)
        }
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

        fun finishTask(context: AppCompatActivity) {
            Log.i(TAG, "clearData")
            WorkManager.getInstance(context).cancelUniqueWork("UploadData")
            clearData()
//            AppPreferences.isHasTask = false
            context.showSuccessComplete().let {
                it.finish_accept_btn.setOnClickListener {
                    context.startActivity(Intent(context, StartWayBillAct::class.java))
                    context.finish()
                }
                it.exit_btn.setOnClickListener {
                    MyUtil.logout(context)
                }
            }
        }

        fun clearData() {
            Log.i(TAG, "clearData")
            baseDat.clearBase()
        }

        fun findPlatforms(): List<PlatformEntity> {
            return baseDat.findPlatforms()
        }

        fun getWayTasks(): List<WorkOrderEntity> {
            return baseDat.findWayTasks()
        }

        fun findLastPlatforms() =
            baseDat.findLastPlatforms()

        fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
            return baseDat.findPlatformByCoordinate(lat, lon)
        }

        fun findCancelWayReason(): List<CancelWayReasonEntity> {
            return baseDat.findCancelWayReason()
        }

        fun findCancelWayReasonByValue(reason: String): Int {
            return baseDat.findCancelWayReasonByValue(reason)
        }

        fun buildMapNavigator(
            currentLocation: Location,
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
                    currentLocation.position,
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


    override fun onLocationUpdated(location: Location) {
//        Log.d("LogDistance", "###################")
        currentLocation = location

        AppPreferences.currentCoordinate = "${location.position.longitude}#${location.position.latitude}"
        AppPreferences.currentCoordinateAccuracy = location.accuracy.toString()
        val distanceToPoint = MyUtil.calculateDistance(location.position, selectedPlatformToNavigate)
//        Log.d("LogDistance", "Distance: $distanceToPoint")
        if (drivingModeState && distanceToPoint <= MIN_METERS && isOnPointFirstTime) {
            isOnPointFirstTime = false
            alertOnPoint().let {
                it.dismiss_btn.setOnClickListener {
                    drivingModeState = false
                    isOnPointFirstTime = true
                    clearMapIbjectsDrive()
                    hideDialog()
                }
            }
        } else {
//            Log.d("LogDistance", "Distance not arrive")
        }
        if (firstTime) {
            moveCameraToUser(location)
            firstTime = false
        }
//        Log.d("LogDistance", "Location updated")
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unsubscribe(locationListener)
        mMapMyYandex.onStop()
        MapKitFactory.getInstance().onStop()
    }



    override fun onResume() {
        super.onResume()
//        mPlatforms = vs.findPlatforms()
        refreshData()
        locationManager = mapKit.createLocationManager()
        locationManager.subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.ON, locationListener)
    }


    @SuppressLint("MissingPermission")
    private fun initUserLocation() {
        mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mMapMyYandex.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
//        userLocationLayer.setObjectListener(this)

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


    override fun onStart() {
        super.onStart()
        mMapMyYandex.onStart()
        MapKitFactory.getInstance().onStart()
    }

}