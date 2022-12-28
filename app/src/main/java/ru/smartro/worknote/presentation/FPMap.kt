package ru.smartro.worknote.presentation

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import net.cachapa.expandablelayout.ExpandableLayout
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.ac.BaseAdapter
import ru.smartro.worknote.ac.PoinT
import ru.smartro.worknote.hideDialog
import ru.smartro.worknote.log.todo.*
import ru.smartro.worknote.MapHelper
import ru.smartro.worknote.MapListener
import ru.smartro.worknote.getActivityProperly
import ru.smartro.worknote.log.RestConnectionResource

class FPMap: AF() , MapPlatformSBehaviorAdapter.PlatformClickListener, MapListener {

    private var mPlatformToServeId: Int? = null
    private var mTimeBeforeInSec: Long = Lnull

    private var mMapMyYandex: MapView? = null

    private var acibNavigatorToggle: AppCompatImageButton? = null
    private var acbUnload: AppCompatImageButton? = null
    private var mAcbGotoComplete: AppCompatButton? = null
    private var mAcbInfo: AppCompatButton? = null
    private var clMapBehavior: ConstraintLayout? = null

    private var mAdapterBottomBehavior: MapPlatformSBehaviorAdapter? = null
    private var mIsAUTOMoveCamera: Boolean = false
    private var mInfoDialog: AlertDialog? = null

    private val vm: VMPserve by activityViewModels()

    private val mWorkOrderFilteredIds: MutableList<Int> = mutableListOf()
    private var mWorkOrderS: List<WorkOrderEntity>? = null
    private var mPlatformS: List<PlatformEntity>? = null
    private var drivingModeState = false

    val mNotifyMap = mutableMapOf<Int, Long>()

    private var MAP: MapHelper? = null
    
    override fun onGetLayout(): Int {
        return R.layout.f_map
    }

    override fun onViewCreated(sview: View, savedInstanceState: Bundle?) {
        super.onViewCreated(sview, savedInstanceState)
        if (!App.getAppliCation().hasPermissions(getAct(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getAct(), PERMISSIONS, 1)
        }

        mMapMyYandex = sview.findViewById(R.id.map_view)
        MAP = MapHelper(mMapMyYandex!!, this)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("startPhotoFailureMedia")?.observe(
            viewLifecycleOwner) { result ->
            LOG.debug("TEST ::: LIVE DATA SAVED STATE: startPhotoFailureMedia: ${result}")
            if(result) {
                val pl = vm.getPlatformEntity()
                findNavController().currentBackStackEntry?.savedStateHandle?.set("startPhotoFailureMedia", false)
                startPhotoFailureMedia(pl)
            }
            // Do something with the result.
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("navigatePlatform")?.observe(
            viewLifecycleOwner) { result ->
            LOG.debug("TEST ::: LIVE DATA SAVED STATE: navigatePlatform: ${result}")
            if(result) {
                val pl = vm.getPlatformEntity()
                findNavController().currentBackStackEntry?.savedStateHandle?.set("navigatePlatform", false)
                navigatePlatform(Point(pl.coordLat, pl.coordLong))
            }
            // Do something with the result.
        }

        val isEmptyBreakDownReasonS = vm.database.findAllBreakDownReasonS().isEmpty()
        val isEmptyCancelWayReasonS = vm.database.findCancelWayReasonEntity().isEmpty()
        val isEmptyFailReasonS = vm.database.findAllFailReason().isEmpty()

        if(isEmptyBreakDownReasonS) {
            saveBreakDownTypes()
        }

        if(isEmptyCancelWayReasonS) {
            saveCancelWayReason()
        }

        if(isEmptyFailReasonS) {
            saveFailReason()
        }

        mAcbInfo = sview.findViewById(R.id.acb_f_map__info)
        mAcbInfo?.setOnClickListener {
            createInfoDialog({})
        }
        setInfoData()

        acbUnload = sview.findViewById(R.id.acb__f_map__unload)
        val carFullStatusButton = sview.findViewById<FrameLayout>(R.id.fl__f_map__car)
        carFullStatusButton.setOnClickListener {
            navigateNext(R.id.LockedCarInfoDF)
        }
        val fuelStatusButton = sview.findViewById<FrameLayout>(R.id.fl__f_map__gas)
        fuelStatusButton.setOnClickListener {
            navigateNext(R.id.DInfoLockedGasF)
        }
        val photoStatusButton = sview.findViewById<FrameLayout>(R.id.fl__f_map__photo)
        photoStatusButton.setOnClickListener {
            navigateNext(R.id.DInfoLockedPhotoF)
        }

        initBottomBehavior(sview)
        
        val fabGotoMyGPS = sview.findViewById<FloatingActionButton>(R.id.fab_f_map__goto_my_gps)
        fabGotoMyGPS.setOnClickListener {
            try {
                AppliCation().startLocationService(true)
                mIsAUTOMoveCamera = true
                MAP?.moveCameraTo(AppliCation().gps())
            } catch (e: Exception) {
                toast("Клиент не найден")
            }
        }

        acibNavigatorToggle = sview.findViewById<AppCompatImageButton>(R.id.acib__f_map__navigator_toggle)
        acibNavigatorToggle?.setOnClickListener {
           clearNavigator()
        }
        val acibGotoLogActMapAPIB = sview.findViewById<AppCompatImageButton>(R.id.goto_log__f_map__apib)
        acibGotoLogActMapAPIB.setOnClickListener {
            navigateNext(R.id.JournalChatFragment, null)
        }

        //todo:  R_dos!!! modeSyNChrON_off(false)
        paramS().isModeSYNChrONize = true
        AppliCation().startWorkER()
        AppliCation().startLocationService()
//        setDevelMode()
                    //         TODO::
                    //        val lottie = view.findViewById<LottieAnimationView>(R.id.lottie)
                    //        lottie.setOnClickListener {
                    ////            lottie.playAnimation()
                    //            lottie.progress = 0.5f
                    //        }
//        setDevelMode()

        vm.todoLiveData.observe(viewLifecycleOwner) {
            LOG.debug("vm.todoLiveData.observe ::: ")
            if (it.coordLat == Dnull) {
                LOG.debug("vm.todoLiveData.observe ::: it.coordLat == Dnull")
                // TODO: !!факТ)
                return@observe
            }

            MAP?.setActivePlatform(it.platformId)
            MAP?.moveCameraTo(PoinT(it.coordLat, it.coordLong))
        }
//        setDevelMode()
        onRefreshData()

        //TODO: сюда изменения вностиьб!
        if (vm.isUnloadMode()) {
            buildNavigatorPlatformUnload()
            navigateNext(R.id.DFModeUnloadTicket)
            toggleUnloadButton(true)
        } else {
            toggleUnloadButton(false)
        }

        acbUnload?.setOnClickListener {
            val platformID = vm.getPlatformId()
            if (platformID == Inull) {
                return@setOnClickListener
            }
            if (vm.isUnloadMode()) {
                navigateNext(R.id.DFModeUnloadTicket)
            } else {
                navigateNext(R.id.UnloadInfoF)
            }
        }
        val userPoint = App.getAppliCation().gps()
        MAP?.moveCameraTo(userPoint)
    }

    fun toggleUnloadButton(isActive: Boolean) {
        val backgroundResource = if(isActive) R.drawable.bg_button__with_caution else R.drawable.bg_button__inactive
        val iconResource = if(isActive) R.drawable.ic_unload_truck__active else R.drawable.ic_unload_truck__inactive

        val backgroundDrawable = ContextCompat.getDrawable(requireContext(), backgroundResource)
        val iconDrawable = ContextCompat.getDrawable(requireContext(), iconResource)

        acbUnload?.background = backgroundDrawable
        acbUnload?.setImageDrawable(iconDrawable)
    }

    fun clearNavigator() {
        drivingModeState = false
        acibNavigatorToggle?.isVisible = drivingModeState
        MAP?.clearMapObjectsDrive()
        hideDialog()
    }


    private fun onRefreshData() {
        LOG.warn("before")
//        mWorkOrderS = getActualWorkOrderS(true)
//        mPlatformS = getActualPlatformS(true)
        LOG.trace("getActualPlatformS.init")
        val platformS = getActualPlatformS(true)
        LOG.trace("getActualPlatformS.end")

        if(platformS.isNotEmpty())
            mPlatformToServeId = platformS[0].platformId

        LOG.trace("onRefreshBottomBehavior.init")
        val platformSWithQueryText = onRefreshBottomBehavior(platformS)
        LOG.trace("onRefreshBottomBehavior.end")
        
        LOG.trace("onRefreshMap.init")
        MAP?.setPlatforms(platformSWithQueryText)
        LOG.trace("onRefreshMap.end")
        
        setInfoData()
        LOG.warn("after")
    }

    private fun setInfoData() {
        val workOrders = getActualWorkOrderS()
        var platformCnt = 0
        var platformProgress = 0
        for (workOrder in workOrders) {
            platformCnt += workOrder.cnt_platform
            platformProgress += workOrder.cnt_platform - workOrder.cnt_platform_status_new
        }
        mAcbInfo?.text = "$platformProgress / $platformCnt"
    }

    private fun getActualWorkOrderS(isForceMode: Boolean = false, isFilterMode: Boolean = true): List<WorkOrderEntity> {
        if (mWorkOrderS == null || isForceMode) {
            LOG.debug("mWorkOrderS == null || isForceMode")
            mWorkOrderS = vm.database.findWorkOrders(isFilterMode)
            if (mWorkOrderS?.isEmpty() == true) {
                LOG.debug("mWorkOrderS?.isEmpty() == true")
                mWorkOrderS = vm.database.findWorkOrders(false)
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

            newPlatformS.sortBy { it.beginnedAt }

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

    }

    private fun saveBreakDownTypes() {
        val breakDownTypeRequest = RGETBreakDownType()
//        breakDownTypeRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
//            LOG.debug("${result}")
//            hideProgress()
//            if (result.isSent) {
////                gotoNextAct()
//            }
//        }
        App.oKRESTman().put(breakDownTypeRequest)
//        vm.networkDat.getBreakDownTypes().observe(getAct()) { result ->
//            when (result.status) {
//                Status.SUCCESS -> {
//                    // TODO: ПО голове себе постучи
//                    LOG.debug("saveBreakDownTypes. Status.SUCCESS")
//                }
//                Status.ERROR -> {
//                    toast(result.msg)
//                }
//                else -> LOG.debug("saveBreakDownTypes:")
//            }
//
//        }
    }

    private fun saveFailReason() {
        LOG.info("saveFailReason.before")
        val failureReasonRequest = RGETFailureReason()
//        failureReasonRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
//            LOG.debug("${result}")
//            hideProgress()
//            if (result.isSent) {
////                gotoNextAct()
//            }
//        }
        App.oKRESTman().put(failureReasonRequest)
//        vm.networkDat.getFailReason().observe(getAct()) { result ->
//            when (result.status) {
//                Status.SUCCESS -> {
//                    LOG.debug("saveFailReason. Status.SUCCESS")
//                }
//                Status.ERROR -> {
//                    toast(result.msg)
//                }
//            }
//        }
    }

    private fun saveCancelWayReason() {
        LOG.debug("saveCancelWayReason.before")
        val workOrderCancelationReasonRequest = RGETWorkOrderCancelationReason()
//        workOrderCancelationReasonRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
//            LOG.debug("${result}")
//            hideProgress()
//            if (result.isSent) {
////                gotoNextAct()
//            }
//        }
        App.oKRESTman().put(workOrderCancelationReasonRequest)
//        vm.networkDat.getCancelWayReason().observe(getAct()) { result ->
//            when (result.status) {
//                Status.SUCCESS -> {
//                    LOG.debug("saveCancelWayReason. Status.SUCCESS")
//                }
//                Status.ERROR -> {
//                    LOG.debug("saveCancelWayReason. Status.ERROR")
//                    toast(result.msg)
//                }
//                else -> {
//                    oops()
//                }
//            }
//        }
    }


    private fun gotoComplete() {
        gotoSynchronize()
    }


    private fun getNextPlatformToSend(next: (nextSentPlatforms: List<PlatformEntity>, timeBefore: Long) -> Any) {
        var nextSentPlatforms: List<PlatformEntity> = emptyList()
        mTimeBeforeInSec = App.getAppliCation().timeStampInSec()
        val lastSynchroTimeInSec = paramS().lastSynchroAttemptTimeInSec
        //проблема в секундах синхронизаций
        val m30MinutesInSec = 30 * 60
        if (App.getAppliCation().timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            mTimeBeforeInSec = lastSynchroTimeInSec + m30MinutesInSec
            nextSentPlatforms = vm.database.findPlatforms30min()
            LOG.debug("SYNCworkER PLATFORMS IN LAST 30 min")
            next(nextSentPlatforms, mTimeBeforeInSec)
        }
        if (nextSentPlatforms.isEmpty()) {
            mTimeBeforeInSec = App.getAppliCation().timeStampInSec()
            nextSentPlatforms = vm.database.findLastPlatforms()
            LOG.debug("SYNCworkER LAST PLATFORMS")
            next(nextSentPlatforms, mTimeBeforeInSec)
        }

    }

    private fun gotoSynchronize() {
        var lastPlatforms: List<PlatformEntity> = emptyList()
        lastPlatforms = vm.database.findLastPlatforms()
        val lastPlatformsSize = lastPlatforms.size

//        val deviceId = Settings.Secure.getString(getAct().contentResolver, Settings.Secure.ANDROID_ID)

        if (lastPlatformsSize > 0) {
            showingProgress()

            val synchroRequest = RPOSTSynchro()
            synchroRequest.getLiveDate().observe(viewLifecycleOwner) { result ->
                LOG.debug("${result}")
                hideProgress()
                if (result is RestConnectionResource.SuccessData) {
                    gotoSynchronize()
                }
            }
            App.oKRESTman().put(synchroRequest)
            

//            getNextPlatformToSend() { nextSentPlatforms, timeBeforeInSec ->
//                showingProgress("отправляются ${nextSentPlatforms.size} КП\n осталось ${lastPlatformsSize}", true)
//                val gps = AppliCation().gps()
//                val synchronizeBody = SynchronizeBody(
//                    paramS().wayBillId,
//                    gps.PointTOBaseData(),
//                    deviceId,
//                    gps.PointTimeToLastKnowTime_SRV(),
//                    PlatformEntity.toSRV(nextSentPlatforms, vm.database)
//                )
//                vm.networkDat.sendLastPlatforms(synchronizeBody, this)
//                Any()
//            }

        } else {
            hideProgress()
            completeWorkOrders()
        }
        logSentry("SYNCworkER STARTED")
        LOG.debug("gotoComplete::synChrONizationDATA:Thread.currentThread().id()=${Thread.currentThread().id}")
    }

    private fun completeWorkOrders() {
        hideInfoDiaLOG()
        navigateNext(R.id.CompleteF)
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

        val logoutButton = view.findViewById<AppCompatImageButton>(R.id.acib__f_map__workorder_info__logout)
        logoutButton.setOnClickListener {
            navigateNext(R.id.DYesNoRelogin)
        }

        val debugButton = view.findViewById<AppCompatImageButton>(R.id.acib__f_map__workorder_info__debug)
        debugButton.setOnClickListener {
            navigateNext(R.id.DebugFragment, null)
            result.dismiss()
        }

        val workOrderS = getActualWorkOrderS(true, isFilterMode = false)
//            var infoText = "**Статистика**\n"
        val rvInfo = view.findViewById<RecyclerView>(R.id.rv_f_map__workorder_info)
        mAcbGotoComplete = view.findViewById(R.id.acb_f_map__workorder_info__gotocomplete)
        mAcbGotoComplete?.setOnClickListener {
            vm.database.setWorkOrderIsShowForUser(workOrderS)
            gotoComplete()
        }
        setAcbCompleteText(workOrderS)

        rvInfo.layoutManager = LinearLayoutManager(getAct())
        val infoAdapter = InfoAdapter(workOrderS)
        rvInfo.adapter = infoAdapter

        builder.setView(view)
        result = builder.create()
        result.setOnCancelListener {
            val workorder: Unit = vm.database.setWorkOrderIsShowForUser(workOrderS)
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
            LOG.error("createInfoDialog", ex)
        }
        mInfoDialog = result
        mInfoDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideInfoDiaLOG()
    }

    private fun hideInfoDiaLOG() {
        try {
            mInfoDialog?.dismiss()
        } catch (ex: Exception) {
            // TODO: 02.11.2021
            LOG.error("hideInfoDialog", ex)
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
        mAdapterBottomBehavior = MapPlatformSBehaviorAdapter(this, platforms, mWorkOrderFilteredIds, rvBehavior) //todo: не заметил линию(;)!!?
        rvBehavior.adapter = mAdapterBottomBehavior
//        rvBehavior.adapter.notifyDataSetChanged()
        val llcBottomHavior = view.findViewById<LinearLayoutCompat>(R.id.act_map__bottom_behavior__header)
        llcBottomHavior.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            App.getAppliCation().hideKeyboard(getAct())
        }
        val acetFilterAddress = view.findViewById<AppCompatEditText>(R.id.acet__f_map__bottom_behavior__filter)
        acetFilterAddress.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                LOG.debug("EXPANDED!!")
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        acetFilterAddress.removeTextChangedListener(null)
        acetFilterAddress.addTextChangedListener { newText ->
            mAdapterBottomBehavior?.let { mapBottom ->
                mapBottom.filteredList(newText.toString())
                MAP?.setPlatforms(mapBottom.getItems())
            }
        }
        acetFilterAddress.clearFocus()
//        svFilterAddress.setOnQueryTextListener(this)
//        svFilterAddress.setOnSearchClickListener{
//            val diP200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200F, resources.displayMetrics)
//                .toInt()
//            svFilterAddress.layoutParams.width = diP200
//
//            LOG.debug("svFilterAddress:::setOnSearchClickListener. ")
//        }
//        svFilterAddress.setOnCloseListener{
//            LOG.debug("svFilterAddress:::setOnCloseListener. before")
//            svFilterAddress.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//            false
//        }
    }

    fun buildNavigatorPlatformUnload(){
        val workOrders = getActualWorkOrderS()

        val coordLat = workOrders.get(0).unload?.coords?.get(0)
        val coordLong = workOrders.get(0).unload?.coords?.get(1)
        coordLat?.let {
            val point = Point(coordLat, coordLong!!)
            buildNavigator(point)
        }
    }

    private fun buildNavigator(checkPoint: Point) {
        try {
//            getMapObjCollection().clear()
            MAP?.buildMapNavigator(AppliCation().gps(), checkPoint)
            drivingModeState = true
            acibNavigatorToggle?.isVisible = drivingModeState
            hideDialog()
            if (clMapBehavior != null) {
                val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            MAP?.moveCameraTo(AppliCation().gps())
        } catch (ex: Exception) {
            LOG.error("buildNavigator", ex)
            toast(getString(R.string.error_build_way))
        }

    }

    private fun showNotificationPlatfrom(platformId: Int, srpId: Long, string: String?) {
        if (mNotifyMap.containsKey(platformId)) {
            return
        }
        mNotifyMap[platformId] = srpId

        val intent = Intent(getAct(), ActMain::class.java)

        // TODO: !!?R_dos(;:)
        intent.putExtra("srpId", srpId)
        intent.putExtra("platform_id", platformId)
        intent.putExtra("mIsServeAgain", false)

        val pendingIntent = getActivityProperly(getAct(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        AppliCation().showNotificationForce(
            pendingIntent,
            "Контейнерная площадка №${srpId}",
            "Вы подъехали к контейнерной площадке",
            "Начать обслуживание ",
            platformId,
            NOTIFICATION_CHANNEL_ID__MAP_ACT
        )
    }

    override fun onInertiaStart() {
        mIsAUTOMoveCamera = false
    }
    
    override fun onPlatformTap(pId: Int) {
        LOG.debug("!!!!!!")
        if(pId == mPlatformToServeId) {
            val platformE = vm.database.getPlatformEntity(pId)
            vm.setPlatformEntity(platformE)
            navigateNext(R.id.MapPlatformClickedDtlF)
        } else {
            navigateNext(R.id.DFPMapWrongServingOrder, pId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LOG.debug("r_dos//onActivityResult.requestCode=${requestCode}")
        LOG.debug("r_dos//onActivityResult.resultCode=${resultCode}")
    }
    
    override fun navigatePlatform(checkPoint: Point) {
        val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
        if (isModeUnload) {
            toast("В режиме выгрузка нельзя обслуживать КП")
            return
        }
//        if (drivingModeState) {
//            navigateNext(R.id.DYesNoClearNavigator)
////            navigateNext(DYesNoClearNavigator::class.javaClass)
//        } else {
        buildNavigator(checkPoint)
//        }
    }


    override fun openFailureFire(item: PlatformEntity) {
        vm.setPlatformEntity(item)
        navigateNext(R.id.PhotoFailureMediaF, item.platformId)
    }


    override fun moveCameraPlatform(item: PlatformEntity) {
        LOG.debug("::: CLICK, plId: ${item.platformId}")

        mIsAUTOMoveCamera = false
        if (clMapBehavior == null) {
            LOG.debug("::: clMapBehavior == null")
            return
        }

        LOG.debug("::: clMapBehavior != null")
        val bottomSheetBehavior = BottomSheetBehavior.from(clMapBehavior!!)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        vm.setPlatformEntity(item)
    }


    override fun onNewGPS() {
        LOG.debug("onNewGPS")

        val point = AppliCation().gps()
        if (mIsAUTOMoveCamera) {
            MAP?.moveCameraTo(point)
        }

        val platformNear = vm.database.findPlatformByCoord(point.latitude, point.longitude, point.getAccuracy())

        if (platformNear == null) {
            LOG.debug("platformNear.is null")
            for ((key, _) in mNotifyMap) {
                AppliCation().cancelNotification(key)
                mNotifyMap.remove(key)
            }
        } else {
            if (!platformNear.isTypoMiB()) {
                // TODO: !!!r_dos
                showNotificationPlatfrom(platformNear.platformId, platformNear.srpId!!, platformNear.name)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mMapMyYandex?.onStop()
        MapKitFactory.getInstance().onStop()
        vm.database.setConfigCntPlusOne(ConfigName.MAPACTDESTROY_CNT)
        vm.database.close()
    }

    inner class InfoAdapter(private var p_workOrderS: List<WorkOrderEntity>) :
        RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.f_map__workorder_info__rv_item, parent, false)
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

    override fun startPlatformBeforeMedia(item: PlatformEntity) {
        val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
        if (isModeUnload) {
            toast("В режиме выгрузка нельзя обслуживать КП")
            return
        }
        if(item.platformId == mPlatformToServeId) {
            val platformE = vm.database.getPlatformEntity(item.platformId)
            vm.setPlatformEntity(platformE)
            navigateNext(R.id.PMapWarnDF, item.platformId)
        } else {
            navigateNext(R.id.DFPMapWrongServingOrderBottom, item.platformId)
        }
    }

    override fun startPhotoFailureMedia(item: PlatformEntity) {
        val isModeUnload = vm.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)
        if (isModeUnload) {
            toast("В режиме выгрузка нельзя обслуживать КП")
            return
        }
        vm.setPlatformEntity(item)
        navigateNext(R.id.PhotoFailureMediaF, item.platformId)
    }

}

class MapPlatformSBehaviorAdapter(
    private val listener: PlatformClickListener,
    mItemS: List<PlatformEntity>,
    private val mFilteredWayTaskIds: MutableList<Int>,
    private val rvBehavior: RecyclerView
) : BaseAdapter<PlatformEntity, MapPlatformSBehaviorAdapter.PlatformViewHolder>(mItemS) {
    private var mOldQueryText: String? = null
    private var lastHolder: PlatformViewHolder? = null

    override fun onGetViewHolder(view: View): PlatformViewHolder {
        return PlatformViewHolder(view)
    }

    override fun onGetLayout(): Int {
        return R.layout.f_map__bottom_behavior__rv_item
    }

    fun filter(platformList: List<PlatformEntity>, filterText: String): List<PlatformEntity> {
        val query = filterText.lowercase()
        val filteredModeList = platformList.filter {
            try {
//                    it.javaClass.getField("address")
                val text = it.address?.lowercase()
                var res = true
                text?.let {
                    res = (text.startsWith(query) || (text.contains(query)))
                }
                res
            } catch (ex: Exception) {
                true
            }
        }
        //            val sYsTEM = mutableListOf<Vehicle>()
        return filteredModeList
    }

    fun filteredList(queryText: String?) {
        // TODO: !R_dos queryText == Snull??
        super.setQueryText(queryText)
        if(queryText.isNullOrEmpty()) {
            super.reset()
            return
        }
        val mItemsAfter = filter(super.getItemsForFilter(), queryText)
        super.set(mItemsAfter)
    }

    fun updateItemS(newItemS: List<PlatformEntity>) {
//        logSentry(filterText)
        super.setItems(newItemS)
        super.setItemsBefore(newItemS)
        lastHolder?.collapseOld()
        filteredList(super.getQueryTextOld())
    }

    private fun setUseButtonStyleBackgroundGreen(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__usebutton))
    }

    private fun setUseButtonStyleBackgroundRed(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_red__usebutton))
    }

    // TODO: ну -Гляди_ держись)
    private fun setDefButtonStyleBackground(view: View) {
//        appCompatButton.alpha = 1f
        view.setBackgroundDrawable(ContextCompat.getDrawable(view.context, R.drawable.bg_button_green__default))
    }

    private fun setUseButtonStyleBackgroundYellow(v: View) {
        v.setBackgroundDrawable(ContextCompat.getDrawable(v.context, R.drawable.bg_button_yellow__usebutton))
    }

    private fun setUseButtonStyleBackgroundOrange(v: View) {
        v.setBackgroundDrawable(ContextCompat.getDrawable(v.context, R.drawable.bg_button_orange__usebutton))
    }

    override fun bind(item: PlatformEntity, holder: PlatformViewHolder) {
        holder.itemView.alpha = 1f
        //фильрация
        if (item.workOrderId in mFilteredWayTaskIds) {
            holder.itemView.alpha = 0.1f
        }
        holder.itemView.findViewById<ExpandableLayout>(R.id.map_behavior_expl).apply {
            if (lastHolder?.platformId == item.platformId) {
                expand(false)
            } else {
                collapse(false)
            }
        }

        holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__address).text = item.address
        val tvName = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__name)
        tvName.isVisible = false
        if (item.name.isShowForUser()) {
            tvName.text = item.name
            tvName.isVisible = true
        }

        val tvOrderTime = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__order_time)
        val orderTime = item.getOrderTime()
        tvOrderTime.isVisible = false
        if (orderTime.isShowForUser()) {
            tvOrderTime.text = orderTime
            tvOrderTime.setTextColor(item.getOrderTimeColor(holder.itemView.context))
            tvOrderTime.isVisible = true
        }

        val tvCurrentStatus = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__status)
        val status = when(item.getStatusPlatform()) {
            StatusEnum.NEW -> "Новое"
            StatusEnum.UNFINISHED -> "Не завершено"
            StatusEnum.SUCCESS -> "Завершено: успешно"
            StatusEnum.PARTIAL_PROBLEMS -> "Завершено: частичный невывоз"
            StatusEnum.ERROR -> "Завершено: невывоз"
            else -> null
        }
        if (status != null)
            tvCurrentStatus.text =status


        holder.itemView.findViewById<TextView>(R.id.map_behavior_scrp_id).text = item.srpId.toString()
        val containerString: String = holder.itemView.context.resources.getQuantityString(R.plurals.container_count, item.containerS.size)
        holder.itemView.findViewById<TextView>(R.id.map_behavior_container_count).text = "${item.containerS.size} $containerString"

        holder.itemView.findViewById<TextView>(R.id.map_behavior_coordinate).setOnClickListener {
            LOG.debug("::: CLICK :::")
            listener.moveCameraPlatform(item)
        }
        holder.itemView.findViewById<ImageButton>(R.id.map_behavior_location).setOnClickListener {
            listener.navigatePlatform(Point(item.coordLat, item.coordLong))
        }

        val tvPlatformContact = holder.itemView.findViewById<TextView>(R.id.tv_item_map_behavior__platform_contact)
        val contactsInfo = item.getContactsInfo()
        tvPlatformContact.text = contactsInfo
        tvPlatformContact.isVisible = contactsInfo.isNotEmpty()
        holder.itemView.setOnClickListener {
            // TODO:
            //nothing
        }

        val currentStatus = item.getStatusPlatform()
        if(currentStatus == StatusEnum.NEW || currentStatus == StatusEnum.UNFINISHED) {
            holder.itemView.apply {
                setOnClickListener {
                    if (!findViewById<ExpandableLayout>(R.id.map_behavior_expl).isExpanded) {
                        holder.acivArrowDropDown.rotation = 180f
                        findViewById<ExpandableLayout>(R.id.map_behavior_expl).expand(true)
                        rvBehavior.postDelayed({
                            rvBehavior.smoothScrollToPosition(holder.adapterPosition)
                        }, 500L)

                        findViewById<Button>(R.id.map_behavior_start_service).setOnClickListener {
                            listener.startPlatformBeforeMedia(item)
                        }

                        findViewById<ImageButton>(R.id.map_behavior_fire).setOnClickListener {
                            listener.startPhotoFailureMedia(item)
                        }

                        if (lastHolder?.platformId != item.platformId) {
                            lastHolder?.collapseOld()
                        }
                        lastHolder = holder
                        lastHolder?.platformId = item.platformId
                    } else {
                        holder.acivArrowDropDown.rotation = 0f
                        findViewById<ExpandableLayout>(R.id.map_behavior_expl).collapse(true)
                    }
                }
            }
        } else {
            holder.acivArrowDropDown.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
        }

        when (currentStatus) {
            StatusEnum.NEW -> {
                setDefButtonStyleBackground(holder.itemView)
            }
            StatusEnum.UNFINISHED -> {
                setUseButtonStyleBackgroundYellow(holder.itemView)
                holder.itemView.findViewById<Button>(R.id.map_behavior_start_service).setText(R.string.start_serve_again)
            }
            StatusEnum.PARTIAL_PROBLEMS -> {
                setUseButtonStyleBackgroundOrange(holder.itemView)
            }
            StatusEnum.SUCCESS -> {
                setUseButtonStyleBackgroundGreen(holder.itemView)
            }
            StatusEnum.ERROR -> {
                setUseButtonStyleBackgroundRed(holder.itemView)
            }
        }
    }

    class PlatformViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var platformId: Int? = null

        val acivArrowDropDown: AppCompatImageView by lazy {
            itemView.findViewById(R.id.aciv_baseline_arrow_drop_down_24)
        }

        fun collapseOld() {
            if (platformId == null) {
                return
            }
            acivArrowDropDown.rotation = 0f
            itemView.findViewById<ExpandableLayout>(R.id.map_behavior_expl)?.collapse(true)
            platformId = null
        }

    }

    interface PlatformClickListener {
        fun startPlatformBeforeMedia(item: PlatformEntity)
        fun startPhotoFailureMedia(item: PlatformEntity)
        fun moveCameraPlatform(item: PlatformEntity)
        fun navigatePlatform(checkPoint: Point)
        fun openFailureFire(item: PlatformEntity)
    }
}
