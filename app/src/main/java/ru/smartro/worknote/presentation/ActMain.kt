package ru.smartro.worknote.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.FAI
import ru.smartro.worknote.ac.IActTooltip
import ru.smartro.worknote.ac.ITooltip
import ru.smartro.worknote.log.todo.ConfigName

//todo: INDEterminate)
class ActMain :
    AAct(), IActTooltip {

    val vm: VMPserve by viewModels()
    private var mIsDlgShown = false

    private val mGpsStateReceiver by lazy { getGpsStateBroadcastReceiver() }

    private fun getGpsStateBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if(intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    LOG.debug("IS GPS ENABLED = ${isGpsEnabled}, IS NETWORK ENABLED = ${isNetworkEnabled}")

                    val dialogShouldShow = (isGpsEnabled || isNetworkEnabled) == false

                    if(dialogShouldShow && mIsDlgShown != dialogShouldShow) {
                        showNextFragment(DFInfoGpsOff.NAV_ID)
                    }

                    mIsDlgShown = dialogShouldShow
                }
            }
        }
    }

//    val mTooltipHell = MainAct.DialogHelpER(this, TAG)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        supportActionBar?.hide()
//        mTooltipHell.setStartId("ll_containers_count", paramS())
    }

    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        (navHostFragment.childFragmentManager.fragments[0] as FAI).onBackPressed()
    }


    override fun createvDialog(): View {
        val vDialog = this.layoutInflater.inflate(R.layout.dialog_act_map_tooltip_cheat__alert, null, false)
        return vDialog
    }

    override fun createDialogBuilder(): androidx.appcompat.app.AlertDialog.Builder {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        return builder
    }
    override fun getvgRootAct(): ViewGroup {
        val vgRootAct = (this.window.decorView.rootView   as ViewGroup)
        return vgRootAct
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED)
        registerReceiver(mGpsStateReceiver, filter)

//        App.getAppliCation().getNavigatorHolder().setNavigator(mNavigator)
    }

//    private  val mNavigator = object : SupportFragmentNavigator(supportFragmentManager, R.id.fragment_container) {
//        override fun createFragment(screenKey: String?, data: Any?): Fragment {
//            mTEMPscreenKeyVAL = screenKey
//            when(screenKey) {
//                SCREEN_EARLY_COMPLETE -> {
//                    val res = FPMap.newInstance(data)
//                    return res
//                }
//                SCREEN_SUCCESS_COMPLETE -> {
//                    val res = FinishCompleteF.newInstance(data)
//                    return res
//                }
////                case default_LIST_SCREEN:
////                return DetailsFragment.getNewInstance(data);
//                else -> return CompleteF.newInstance(data)
////                throw new default     RuntimeException(???Unknown ???????????? key!???);
//            }
//        }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGpsStateReceiver)
        LOG.info("SWIPE")
        vm.database.setConfigCntPlusOne(ConfigName.SWIPE_CNT)
        App.getAppliCation().getNavigatorHolder().removeNavigator()
    }

    override fun onRestart() {
        super.onRestart()
    }



    //todo:::
    override fun onDestroy() {
        super.onDestroy()

        hideDialog()
//        mTooltipHell.setNextTime(paramS())

    }

    final override fun onNewfromAFragment(isFromRecylcer: Boolean) {
//        super.onNewfromAFragment()
//        if (mTooltipHell.isRecyclerMode) {
//            if (isFromRecylcer) {
//                mTooltipHell.run()
//            }
//        } else {
//            mTooltipHell.run()
//        }

    }

    override fun setSpecialProcessingForRecycler(recyclerView: RecyclerView?) {
//        mTooltipHell.isRecyclerMode = true
//        recyclerView?.let {
//            it.viewTreeObserver.addOnGlobalLayoutListener(
//                object : ViewTreeObserver.OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        // At this point the layout is complete and the
//                        // dimensions of recyclerView and any child views
////                            onNewfromAFragment()
//                        onNewfromAFragment(true)
//                        // are known.
//                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        mTooltipHell.isRecyclerMode = false
//                    }
//                })
//        }
    }
//

    class DialogHelpER(val actTooltip: IActTooltip, val p_valim: String="TooltipHelpER") : AbsObject(p_valim) {
        var isRecyclerMode: Boolean = false
        private var mTooltipNextId: String? = null
        private var vDialog: View? = null

        fun run(isFromRecylcer: Boolean = false) {
            if (isShowForUser()) {
                val vgRootAct = actTooltip.getvgRootAct()
                vDialog = actTooltip.createvDialog()
                val llcDialogView = vDialog?.findViewById<LinearLayoutCompat>(R.id.llc_component_container)
                val viewScanner = createViewScanner(isFromRecylcer)
                llcDialogView?.let {
                    viewScanner.findNextTooltip(vgRootAct, llcDialogView) { tooltipText ->
                        showTooltipDialogAndText(tooltipText, viewScanner)
                    }
                }
            }
        }

        private fun showTooltipDialogAndText(tooltipText: String, viewScanner: ViewScaner) {
            val builderDialog = actTooltip.createDialogBuilder()
            builderDialog.setView(vDialog)
            val createdDialog = builderDialog.create()
            vDialog?.findViewById<AppCompatButton>(R.id.apb_dialog)?.setOnClickListener {
                createdDialog.dismiss()
            }
            createdDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            createdDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            createdDialog.window?.setDimAmount(0.8f); //0 for no dim to 1 for full dim

            val actvDialog = vDialog?.findViewById<AppCompatTextView>(R.id.actv_dialog)
            actvDialog?.text = tooltipText

            createdDialog.setOnDismissListener {
                val nextTooltipAsText = viewScanner.revertSaveView()
                nextTooltipAsText?.let {
//                        gogogo(it)
//                        viewScanner.findNextTooltip(vgRootAct, ll)
                    this.run()
                }
            }
            createdDialog.show()
        }

        fun isShowForUser(): Boolean{
            return getNextTooltipId() != null
        }
        fun getNextTooltipId(): String? {
            return mTooltipNextId
        }

        fun createViewScanner(isFromRecylcer: Boolean): ViewScaner {
            return ViewScaner(this, isFromRecylcer)
        }


        fun setStartId(viewIdAsText: String, paramS: AppParaMS) {
            if(paramS.isShowTooltipInNextTime) {
//            navigateNext(WalkthroughStepAF.NAV_ID, 1)
                setNextId(viewIdAsText)
                paramS.isShowTooltipInNextTime = false
            }
        }
        fun setNextId(smartROView: ITooltip) {
            LOG.debug("getWindow(.smartROView.getTooltipNext()=${smartROView.getTooltipNext()}")
            LOG.debug("getWindow(.smartROView.getTooltipType()=${smartROView.getTooltipType()}")
            setNextId(smartROView.getTooltipNext())
        }

        fun setNextId(tooltipIdAsText: String?) {
            mTooltipNextId = tooltipIdAsText
        }

        fun setNextTime(paramS: AppParaMS) {
            paramS.cntTooltipShow += 1
            if(paramS.cntTooltipShow < 2) {
                paramS.isShowTooltipInNextTime = true
            }
        }
    }

    class ViewScaner(val _DialogHelper: DialogHelpER, val isFromRecylcer: Boolean) : AbsObject("${_DialogHelper.p_valim}:ViewScaner") {
        var mStopScan: Boolean = false
        var mSaveObj: View? = null
        var mSaveIndex: Int? = null
        var mLayoutParams: ViewGroup.LayoutParams? = null
        var mSaveVG: ViewGroup? = null

        private var vgRootAct: ViewGroup? = null
        private var llcDialogView: ViewGroup? = null


        fun getSaveView(): View {
            return mSaveObj!!
        }

        fun findNextTooltip(vgRootAct: ViewGroup, llcDialogView: ViewGroup, next:(tooltipText: String) -> Any){
            this.llcDialogView = llcDialogView
            this.vgRootAct = vgRootAct
            val tooltipNextId = _DialogHelper.getNextTooltipId()!!

            val findView = scanViewGroup(vgRootAct, tooltipNextId)
            //        findView?.let {
            //            ll.addView(findView)
            //        }

//        tomDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (mSaveObj == null) {
                return
            }
            next(mSaveObj?.tooltipText.toString())
        }

        private fun pickupSaveView(currentVG: ViewGroup, obj: View) {
            mSaveVG = currentVG
            mSaveObj = obj
            mLayoutParams = obj.layoutParams
            for (index in 0 until currentVG.childCount) {
                if (currentVG.getChildAt(index).equals(obj)) {
                    mSaveIndex = index
                    break
                }
            }
            currentVG.removeView(obj)
            llcDialogView?.addView(obj)
            obj.isEnabled = false
        }

        fun revertSaveView(): String? {
            if (mSaveObj == null) {
                return null
            }
//                    val viewGroupObjects: Map<View, View> = mutableMapOf()
            llcDialogView?.removeView(mSaveObj)
            mSaveVG?.addView(mSaveObj, mSaveIndex!!)

            mSaveObj?.layoutParams = mLayoutParams
            mSaveObj?.isEnabled = true


            mStopScan = false
            mSaveObj = null
            mSaveIndex= null
            mLayoutParams = null
            mSaveVG = null
            return _DialogHelper.getNextTooltipId()
        }

        private fun scanViewGroup(currentVG: ViewGroup, tooltipNextID: String) {
            val childCnt = currentVG.childCount
            for (idx in 0 until childCnt) {
                val obj = currentVG.getChildAt(idx)
                LOGWork("getWindow(.obj.isFromRecylcer=${isFromRecylcer}")
//                if (!isFromRecylcer && obj is RecyclerView) {
//                    _DialogHelper.setSpecialProcessingForRecycler(obj)
//                    mStopScan = true
//                    return
//                }
                if (obj is ITooltip) {
                    LOGWork("getWindow(.obj.getIdText()=${obj.getIdText()}")
                    if(obj.getIdText() == tooltipNextID) {
                        LOGWork("getWindow(.tooltipNextID()=${tooltipNextID}")
                        pickupSaveView(currentVG, obj)
                        _DialogHelper.setNextId(obj)

                        mStopScan = true
                        return
                    }
                    LOGWork("getWindow(.obj.id=${obj.id}")
                    LOGWork("getWindow(:${obj.tag}")
                    LOGWork("getWindow(:::")
                }

                if ( obj is ViewGroup) {
                    val Vg = obj as ViewGroup
                    LOGWork("getWindow($idx)Vg.childCount=${Vg.childCount}")
                    if (mStopScan) {
                        LOGWork("getWindow($idx)Vg._DialogHelper.mStopScan")
                        break
                    }
                    LOGWork("getWindow($idx)Vg.childCount=${Vg.tag}")
                    scanViewGroup(Vg, tooltipNextID)
                }
            }
        }


    }

}