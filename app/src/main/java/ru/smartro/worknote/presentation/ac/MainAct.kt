package ru.smartro.worknote.presentation.ac

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.TAG
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.andPOintD.IActTooltip
import ru.smartro.worknote.andPOintD.ITooltip
import ru.smartro.worknote.log
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM

//todo: INDEterminate)
class MainAct :
    AAct(), IActTooltip {
    val vm: ServePlatformVM by viewModels()
    val mTooltipHell = MainAct.DialogHelpER(this, TAG)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        supportActionBar?.hide()

        mTooltipHell.setStartId("ll_containers_count", paramS())
    }


    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        (navHostFragment.childFragmentManager.fragments[0] as AFragment).onBackPressed()
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

    //todo:::
    override fun onDestroy() {
        super.onDestroy()
        mTooltipHell.setNextTime(paramS())

    }

    final override fun onNewfromAFragment(isFromRecylcer: Boolean) {
//        super.onNewfromAFragment()
        if (mTooltipHell.isRecyclerMode) {
            if (isFromRecylcer) {
                mTooltipHell.run()
            }
        } else {
            mTooltipHell.run()
        }

    }

    override fun setSpecialProcessingForRecycler(recyclerView: RecyclerView?) {
        mTooltipHell.isRecyclerMode = true
        recyclerView?.let {
            it.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        // At this point the layout is complete and the
                        // dimensions of recyclerView and any child views
//                            onNewfromAFragment()
                        onNewfromAFragment(true)
                        // are known.
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        mTooltipHell.isRecyclerMode = false
                    }
                })
        }
    }
//

    class DialogHelpER(val actTooltip: IActTooltip, val p_TAG: String, val p_valim: String="TooltipHelpER") : AbsObject(p_TAG, p_valim) {
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
//            navigateMain(R.id.WalkthroughStepAF, 1)
                setNextId(viewIdAsText)
                paramS.isShowTooltipInNextTime = false
            }
        }
        fun setNextId(smartROView: ITooltip) {
            log("getWindow(.smartROView.getTooltipNext()=${smartROView.getTooltipNext()}")
            log("getWindow(.smartROView.getTooltipType()=${smartROView.getTooltipType()}")
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

    class ViewScaner(val _DialogHelper: DialogHelpER, val isFromRecylcer: Boolean) : AbsObject(_DialogHelper.p_TAG, "${_DialogHelper.p_valim}:ViewScaner") {
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