package ru.smartro.worknote.ui.map

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.av.verticalchipgroup.CustomChipGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.*
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
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.problem.CancelWayReasonEntity
import ru.smartro.worknote.service.network.Resource
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.ui.choose.way_list_3.WayBillActivity
import ru.smartro.worknote.ui.debug.DebugActivity
import ru.smartro.worknote.ui.journal.JournalAct
import ru.smartro.worknote.ui.platform_serve.PlatformServeActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.StatusEnum
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.SynchronizeWorker
import ru.smartro.worknote.work.WayTaskEntity
import ru.smartro.worknote.work.Workorder
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.math.round


class MapAct : AbstractAct(),
    /*UserLocationObjectListener,*/ MapObjectTapListener,
    PlatformAdapter.PlatformClickListener, LocationListener {
    private lateinit var mEndWorkOrder: AppCompatButton
    private lateinit var mLlcMap: LinearLayoutCompat
    private val mFilteredWayTaskIds: MutableList<Int> = mutableListOf()
    private lateinit var mPlatforms: List<PlatformEntity>
    var drivingModeState = false

    private val REQUEST_EXIT = 41
    private var firstTime = true
    private var isOnPointFirstTime = true
    private val vm: MapViewModel by viewModel()

    private val mapObjectTapListener = this as MapObjectTapListener

    private val locationListener = this as LocationListener
    private val MIN_METERS = 50

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var drivingSession: DrivingSession.DrivingRouteListener
    private lateinit var currentLocation: Location
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var selectedPlatformToNavigate: Point
    private lateinit var locationManager: LocationManager
    private lateinit var mapKit: MapKit

    private fun saveBreakDownTypes() {
        vm.getBreakDownTypes().observe(this, Observer { result ->
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
        vm.getFailReason().observe(this, Observer { result ->
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

    private fun saveCancelWayReason() {
        Log.d(TAG, "saveCancelWayReason.before")
        vm.getCancelWayReason().observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveCancelWayReason. Status.SUCCESS")
                }
                Status.ERROR -> {
                    Log.d(TAG, "saveCancelWayReason. Status.ERROR")
                    toast(result.msg)
                }
            }
        })
    }

    private fun acceptProgress(workorder: Workorder): Resource<EmptyResponse> {
        Log.d(TAG, "acceptProgress.before")
        val res = vm.progress(workorder.id, ProgressBody(MyUtil.timeStamp()))
        return res

    }

    fun gotoProgressWorkOrder(workorders: List<Workorder>) {
//        AppPreferences.wayTaskId = workorder.id
        loadingShow()
        try {
            saveFailReason()
            saveCancelWayReason()
            saveBreakDownTypes()
//                    val hand = Handler(Looper.getMainLooper())
            for (workorder in workorders) {
                logSentry(workorder.name)
                val result = acceptProgress(workorder)
                when (result.status) {
                    Status.SUCCESS -> {
                        logSentry("acceptProgress Status.SUCCESS ")
                        AppPreferences.isHasTask = true
                        vm.insertWayTask(workorder)
                    }
                    else -> {
                        logSentry( "acceptProgress Status.ERROR")
                        toast(result.msg)
                        AppPreferences.isHasTask = false
                        break
                    }
                }
            }
        } finally {
            loadingHide()

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        setContentView(R.layout.act_map)



//        val chipGroup = findViewById<ChipGroup>(R.id.chip_group)
//        val textView = TextView(this)
//        textView.text = "AAAAAAAAAAAAAAAAAAAa"
//        chipGroup.addView(textView)

        mLlcMap = findViewById<LinearLayoutCompat>(R.id.llc_activity_map)
        mEndWorkOrder = findViewById<AppCompatButton>(R.id.acb_activity_map__end_workorder)
        mLlcMap.isVisible = false
//        mEndWorkOrder.isVisible = false
        mPlatforms = vm.findPlatforms()
//        initChipGroup()
        initSynchronizeWorker()
        initMapView(false)
        initBottomBehavior()
        initUserLocation()
        initDriveMode()

        // TODO: 21.10.2021 r_dos 
//        map_toast.setOnClickListener{
//            toast("${AppPreferences.wayBillId}")
//        }
    }

    private fun initChipGroup() {
        val inflater = LayoutInflater.from(this)
        val chipGroup = findViewById<View>(R.id.chipGroup) as CustomChipGroup
        val wayTasks = vm.getWayTasks()
        for (wayTask in wayTasks) {
            val newChip = inflater.inflate(R.layout.act_map__workorder__checkbox, chipGroup, false) as Chip
            newChip.text = wayTask.name
            newChip.tag = wayTask.id
            chipGroup.addView(newChip)
            newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mFilteredWayTaskIds.add(wayTask.id!!)
                } else {
                    mFilteredWayTaskIds.remove(wayTask.id!!)
                }
                initMapView(true)
                initBottomBehavior()
            }
        }
    }

    private fun initDriveMode() {
        selectedPlatformToNavigate = Point(0.0, 0.0)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = map_view.map.mapObjects.addCollection()
        drivingSession = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutesError(p0: Error) {
                toast("Ошибка при построении маршрута")
            }

            override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
                routes.forEach { mapObjects.addPolyline(it.geometry) }
            }

        }
        navigator_toggle_fab.setOnClickListener {
            drivingModeState = false
            navigator_toggle_fab.isVisible = drivingModeState
            mapObjects.clear()
            hideDialog()
        }

        log_fab.setOnClickListener {
            startActivity(Intent(this@MapAct, JournalAct::class.java))
        }

    }

    private fun initSynchronizeWorker() {
        Log.w(TAG, "initSynchronizeWorker.before thread_id=${Thread.currentThread().id}")
        AppPreferences.workerStatus = true
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SynchronizeWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("UploadData", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
        Log.d(TAG, "initSynchronizeWorker.after")
    }

    @SuppressLint("MissingPermission")
    private fun initUserLocation() {
        mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(map_view.mapWindow)
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

    private fun initMapView(isUpdateView: Boolean) {
        mPlatforms.let {
            val mapObjectCollection = map_view.map.mapObjects
            if (isUpdateView) {
                mapObjectCollection.clear()
                mapObjectCollection.removeTapListener(mapObjectTapListener)
            }

            addPlaceMarks(this, mapObjectCollection, it)
            mapObjectCollection.addTapListener(mapObjectTapListener)
        }

    }

    private fun getIconViewProvider(_context: Context, _platform: PlatformEntity): ViewProvider {
        val result = layoutInflater.inflate(R.layout.map_activity__iconmaker, null)
        val iv = result.findViewById<ImageView>(R.id.map_activity__iconmaker__imageview)

        iv.setImageDrawable(ContextCompat.getDrawable(_context, _platform.getIconDrawableResId()))


//            iv.backgroundd==
//            val resultIcon =  View(_context).apply { background =  }
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

        if (_platform.workorderId in mFilteredWayTaskIds) {
            iv.alpha = 0.1f
            result.alpha = 0.1f
            tv.alpha = 0.1f
        }

        return ViewProvider(result)
    }


//        int picSize = {нужный вам размер изображения};
//        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        // отрисовка плейсмарка
//        Paint paint = new Paint();
//        paint.setColor(Color.Green);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
//        // отрисовка текста
//        paint.setColor(Color.WHITE)
//        paint.setAntiAlias(true);
//        paint.setTextSize({Нужный размер текста});
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(number, picSize / 2,
//            picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
//        return bitmap;


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

    private fun addPlaceMarks(context: Context, mapObjectCollection: MapObjectCollection, platforms: List<PlatformEntity>) {
//        val source = BitmapFactory.decodeResource(context.resources, R.drawable.your_icon_name)
// создаем mutable копию, чтобы можно было рисовать поверх
// создаем mutable копию, чтобы можно было рисовать поверх
//        val bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
// инициализируем канвас
// инициализируем канвас
//        val canvas = Canvas(bitmap)
// рисуем текст на канвасе аналогично примеру выше

        platforms.forEach {
            mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it))
//            mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!),
//                ImageProvider.fromBitmap(drawSimpleBitmap("number")))
        // TODO: 22.10.2021 wtf???!!!start
//            when (it.status) {
//                StatusEnum.NEW -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.SUCCESS -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.ERROR -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//                StatusEnum.UNFINISHED -> {
//                    mapObjectCollection.addPlacemark(Point(it.coords[0]!!, it.coords[1]!!), getIconViewProvider(context, it.getIconDrawableResId()))
//                }
//            }
            // TODO: 22.10.2021 WTF?!!!stop

        }

    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
        MapKitFactory.getInstance().onStart()
    }


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

    private fun initBottomBehavior() {
        mPlatforms.let {

            bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
            val bottomSheetBehavior = BottomSheetBehavior.from(map_behavior)
            val platformsArray = it
            platformsArray.sortedBy { it.updateAt }
            map_behavior_rv.adapter = PlatformAdapter(this, platformsArray, mFilteredWayTaskIds)

            act_map__bottom_behavior__header.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                else
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            val hasNotServedPlatform = it.any { found -> found.status == StatusEnum.NEW }
            if (hasNotServedPlatform) {
                map_behavior_send_btn.background = getDrawable(R.drawable.bg_button_red)
                map_behavior_send_btn.text = getString(R.string.finish_way_now)
            } else {
                map_behavior_send_btn.background = getDrawable(R.drawable.bg_button)
                map_behavior_send_btn.text = getString(R.string.finish_way)
            }
            map_behavior_send_btn.setOnClickListener {
                val lastPlatforms = vm.findLastPlatforms()
                if (lastPlatforms.isEmpty()) {
                    finishWay(hasNotServedPlatform)
                } else {

                    var long = 0.0
                    var lat = 0.0
                    val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                    val currentCoordinate = AppPreferences.currentCoordinate
                    if (currentCoordinate.contains("#")) {
                        long = currentCoordinate.substringAfter("#").toDouble()
                        lat = currentCoordinate.substringBefore("#").toDouble()
                    }
                    val synchronizeBody = SynchronizeBody(AppPreferences.wayBillId, listOf(lat, long), deviceId, lastPlatforms)
                    warningAlert("Не все данные отправлены нa сервер").let {
                        it.accept_btn.setOnClickListener {
                            loadingShow()
                            vm.sendLastPlatforms(synchronizeBody).observe(this, { result ->
                                    when (result.status) {
                                        Status.SUCCESS -> {
                                            loadingHide()
                                            hideDialog()
                                            finishWay(hasNotServedPlatform)
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
                        }
                    }
                }
            }
        }
    }



    private fun finishWay(boolean: Boolean) {
        if (!boolean) {
            successCompleteWayBill()
        } else {
            earlyCompleteWayBill()
        }
    }

    private fun earlyCompleteWayBill() {
        val allReasons = vm.findCancelWayReason()
        showDialogEarlyComplete(allReasons).let { view ->
            val totalVolume = vm.findContainersVolume()
            view.unload_value_et.setText("$totalVolume")
            view.accept_btn.setOnClickListener {
                if (!view.reason_et.text.isNullOrEmpty() &&
                    (view.early_volume_tg.isChecked || view.early_weight_tg.isChecked)
                    && !view.unload_value_et.text.isNullOrEmpty()
                ) {
                    val failureId = vm.findCancelWayReasonByValue(view.reason_et.text.toString())
                    val unloadValue = round(
                        view.unload_value_et.text.toString().toDouble() * 100
                    ) / 100
                    val unloadType = if (view.early_volume_tg.isChecked) 1 else 2
                    val body = EarlyCompleteBody(failureId, MyUtil.timeStamp(), unloadType, unloadValue)
                    loadingShow()

                    vm.earlyComplete(AppPreferences.wayTaskId, body)
                        .observe(this@MapAct, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    vm.finishTask(this)
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

    private fun successCompleteWayBill() {
        val totalVolume = vm.findContainersVolume()
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
                    loadingShow()
                    vm.completeWay(AppPreferences.wayTaskId, body)
                        .observe(this@MapAct, Observer { result ->
                            when (result.status) {
                                Status.SUCCESS -> {
                                    vm.finishTask(this@MapAct)
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
        val intent = Intent(this, PlatformServeActivity::class.java)
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
        map_view.map.move(CameraPosition(point, 16.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 1F), null)
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
            mapObjects.clear()
            selectedPlatformToNavigate = checkPoint
            vm.buildMapNavigator(currentLocation, checkPoint, drivingRouter, drivingSession)
            drivingModeState = true
            navigator_toggle_fab.isVisible = drivingModeState
            hideDialog()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            moveCameraToUser(currentLocation)
        } catch (e: Exception) {
            toast(getString(R.string.error_build_way))
        }

    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        mPlatforms = vm.findPlatforms()
        initMapView(true)
        initBottomBehavior()
        locationManager = mapKit.createLocationManager()
        locationManager.subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.ON, locationListener)
    }

    private fun moveCameraToUser(location: Location) {
        map_view.map.move(
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

    override fun onLocationUpdated(location: Location) {
//        Log.d("LogDistance", "###################")
        currentLocation = location
        AppPreferences.currentCoordinate = "${location.position.longitude}#${location.position.latitude}"
        val distanceToPoint = MyUtil.calculateDistance(location.position, selectedPlatformToNavigate)
//        Log.d("LogDistance", "Distance: $distanceToPoint")
        if (drivingModeState && distanceToPoint <= MIN_METERS && isOnPointFirstTime) {
            isOnPointFirstTime = false
            alertOnPoint().let {
                it.dismiss_btn.setOnClickListener {
                    drivingModeState = false
                    isOnPointFirstTime = true
                    mapObjects.clear()
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
        map_view.onStop()
        MapKitFactory.getInstance().onStop()
    }


    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val placeMark = mapObject as PlacemarkMapObject
        val coordinate = placeMark.geometry
        val clickedPlatform = vm.findPlatformByCoordinate(lat = coordinate.latitude, lon = coordinate.longitude)
        val platformClickedDtlDialog = PlatformClickedDtlDialog(clickedPlatform, coordinate)
        platformClickedDtlDialog.show(supportFragmentManager, "PlaceMarkDetailDialog")
        return true
    }




    /**
    ))НА//СМЕХОПАНОРАМА.яRостb.кИвИн, НО за ТОЛЬКО СРАЗУ же галка]точка[очно даже к гадалке не ходиТЕ
    ))на//СМЕХОПАНОРАМА.яРость.КВН, НО за ТОЛЬКО СРАЗУ же гал]ка+оч[но к гадалке не ходи D.а/же
    возьмите вот говор вот простонародье вот суть Мне(мне)и точно не мне)ярость не про r_dos::mafka и R_dos
    http://Я́РОСТЬ, -и, ж Состояние сильного недовольства, крайнего возмущения кем-, чем-л.; Син.: гнев, бешенство, раздражение.
    Mafka.Ева/s/,А галака не гадалка,
     */

    open class MapViewModel(application: Application) : BaseViewModel(application) {


        fun completeWay(id: Int, completeWayBody: CompleteWayBody): LiveData<Resource<EmptyResponse>> {
            return network.completeWay(id, completeWayBody)
        }

        fun earlyComplete(id: Int, body: EarlyCompleteBody): LiveData<Resource<EmptyResponse>> {
            return network.earlyComplete(id, body)
        }

        fun finishTask(context: AppCompatActivity) {
            Log.i(TAG, "clearData")
            context.loadingHide()
            WorkManager.getInstance(context).cancelUniqueWork("UploadData")
            clearData()
            AppPreferences.isHasTask = false
            context.showSuccessComplete().let {
                it.finish_accept_btn.setOnClickListener {
                    context.startActivity(Intent(context, WayBillActivity::class.java))
                    context.finish()
                }
                it.exit_btn.setOnClickListener {
                    MyUtil.logout(context)
                }
            }
        }

        fun clearData() {
            Log.i(TAG, "clearData")
            db.clearData()
        }

        fun findPlatforms(): List<PlatformEntity> {
            return db.findPlatforms()
        }

        fun getWayTasks(): List<WayTaskEntity> {
            return db.findWayTasks()
        }

        fun findLastPlatforms() =
            db.findLastPlatforms()

        fun findPlatformByCoordinate(lat: Double, lon: Double): PlatformEntity {
            return db.findPlatformByCoordinate(lat, lon)
        }

        fun sendLastPlatforms(body: SynchronizeBody): LiveData<Resource<SynchronizeResponse>> {
            return network.sendLastPlatforms(body)
        }

        fun findCancelWayReason(): List<CancelWayReasonEntity> {
            return db.findCancelWayReason()
        }

        fun findCancelWayReasonByValue(reason: String): Int {
            return db.findCancelWayReasonByValue(reason)
        }

        fun findContainersVolume(): Double =
            db.findContainersVolume()

        fun buildMapNavigator(currentLocation: Location,
                              checkPoint: Point, drivingRouter: DrivingRouter,
                              drivingSession: DrivingSession.DrivingRouteListener) {
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

        fun getBreakDownTypes(): LiveData<Resource<Nothing>> {
            return network.getBreakDownTypes()
        }


        fun getFailReason(): LiveData<Resource<Nothing>> {
            return network.getFailReason()
        }

        fun getCancelWayReason(): LiveData<Resource<Nothing>> {
            return network.getCancelWayReason()
        }

        fun insertWayTask(response: Workorder) {
            db.insertWayTask(response)
        }

        fun progress(id: Int, body: ProgressBody): Resource<EmptyResponse> {
            return network.progress(id, body)
        }


    }








}