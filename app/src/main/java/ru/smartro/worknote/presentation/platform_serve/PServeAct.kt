package ru.smartro.worknote.presentation.platform_serve

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.andPOintD.ITooltip

//todo: INDEterminate)
class PServeAct :
    ActNOAbst() {
    val vm: PlatformServeSharedViewModel by viewModels()
    val mTooltipHell = TooltipHell(TAG, "TooltipHelpER")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_platformserve)
        supportActionBar?.hide()
        val platformId = intent.getIntExtra("platform_id", Inull)
        val modeTMP_know1 = intent.getStringExtra("mode")?: Snull

        val bundle = Bundle()
        bundle.putInt("ARGUMENT_NAME___PARAM_ID", platformId)
        val navController = (supportFragmentManager.findFragmentById(R.id.f_container) as NavHostFragment).navController

        if(modeTMP_know1 == "itFireMode") {
            navController.navigate(R.id.FAStPhotoFailureMediaF, bundle)
            return
        }
        if(vm.mBeforeMediaWasInited.value == false) {

            navController.navigate(R.id.PhotoBeforeMediaF, bundle)
//            setupActionBarWithNavController(navController)
            vm.mBeforeMediaWasInited.postValue(true)
        }
        mTooltipHell.setStartId("ll_containers_count")
    }


    private var mBackPressedCnt: Int = 2

    // TODO: !~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun onBackPressed() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.f_container) as NavHostFragment)
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id == R.id.PServeF) {
            mBackPressedCnt--
            if (mBackPressedCnt <= 0) {
                (navHostFragment.getChildFragmentManager().getFragments().get(0) as AFragment).onBackPressed()
                super.onBackPressed()
                return
            }
            toast("Вы не завершили обслуживание КП. Нажмите ещё раз, чтобы выйти")
            return
        }

        (navHostFragment.getChildFragmentManager().getFragments().get(0) as AFragment).onBackPressed()
//        super.onBackPressed()
    }

    //todo:::
    override fun onDestroy() {
        super.onDestroy()
        mTooltipHell.setNextTime()

    }

    override fun onNewfromAFragment() {
        super.onNewfromAFragment()


        if (mTooltipHell.isShowForUser()) {
            val builder = AlertDialog.Builder(this@PServeAct)

            val vgDialog = this.layoutInflater.inflate(R.layout.dialog_act_map_tooltip_cheat__alert, null, false)


            val ll = vgDialog.findViewById<LinearLayoutCompat>(R.id.llc_component_container)

            val vgRootAct = (this@PServeAct.window.decorView.rootView   as ViewGroup)
            mTooltipHell.findNextTooltip(vgRootAct, ll) {
                builder.setView(vgDialog)
                val tomDialog = builder.create()
                vgDialog.findViewById<AppCompatButton>(R.id.apb_dialog).setOnClickListener {
                    tomDialog.dismiss()
                }
                tomDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                tomDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
                tomDialog.window?.setDimAmount(0.8f); //0 for no dim to 1 for full dim

                val actvDialog = vgDialog.findViewById<TextView>(R.id.actv_dialog)
                actvDialog.text = it.getSaveView().tooltipText
                tomDialog.show()
            }
        }
    }


    class TooltipHell(TAG: String, valim: String) : AbsObject(TAG, valim) {
        private var mStopScan: Boolean = false
        private var mSaveObj: View? = null
        private var mSaveIndex: Int? = null
        private var mLayoutParams: ViewGroup.LayoutParams? = null

        private var mSaveVG: ViewGroup? = null
        private var mTooltipNextId: String? = null

        fun getSaveView(): View {
            return mSaveObj!!
        }
        fun isShowForUser(): Boolean{
            return getNextTooltipId() != null
        }
        fun getNextTooltipId(): String? {
            return mTooltipNextId
        }
        fun findNextTooltip(rootViewGroup: ViewGroup, rootDialogViewGroup: ViewGroup, next:(it: TooltipHell) -> Any){
            val tooltipNextId = getNextTooltipId()!!
            mStopScan = false
            mSaveIndex = null
            mSaveVG = null
            mSaveObj = null
            mLayoutParams = null
            val findView = fractalS(rootViewGroup.childCount, rootViewGroup, rootDialogViewGroup, tooltipNextId)
            mStopScan = false
            //        findView?.let {
            //            ll.addView(findView)
            //        }

//        tomDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mSaveObj?.let {
               next(this)

                tomDialog.setOnDismissListener {
                    if (mSaveObj == null) {
                        return@setOnDismissListener
                    }
                    ll.removeView(mSaveObj)
//                    val viewGroupObjects: Map<View, View> = mutableMapOf()
                    mSaveVG?.addView(mSaveObj, mSaveIndex!!)

                    mSaveObj?.layoutParams = mLayoutParams
                    mSaveObj?.isEnabled = true
                    mTooltipNextId?.let {
                        gogogo(it)
                        mStopScan = false
                    }
                }
            }

        }

        private fun fractalS(childCnt: Int, currentVG: ViewGroup, ll: ViewGroup, tooltipNextID: String) {
            for (idx in 0 until childCnt) {
                val obj = currentVG.getChildAt(idx)
                if (obj is ITooltip) {
                    if(obj.getIdText() == tooltipNextID) {
                        setSpecialProcessingForRecycler(obj)
                        mSaveVG = currentVG
                        mSaveObj = obj
                        mLayoutParams = mSaveObj?.layoutParams
                        for (index in 0 until currentVG.childCount) {
                            if (currentVG.getChildAt(index).equals(mSaveObj)) {
                                mSaveIndex = index
                                break
                            }
                        }
                        currentVG.removeView(obj)
                        ll.addView(obj)
                        obj.isEnabled = false
                        LOGWork("getWindow(.obj.getIdText()=${obj.getIdText()}")
                        setNextId(obj.getTooltipNext())

                        LOGWork("getWindow(.obj.getTooltipNext()=${obj.getTooltipNext()}")
                        LOGWork("getWindow(.obj.getTooltipType()=${obj.getTooltipType()}")
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
                    if (mStopScan == false) {
                        LOGWork("getWindow($idx)Vg.childCount=${Vg.tag}")
                        fractalS(Vg.childCount, Vg, ll, tooltipNextID)
                    }
                }
            }
        }
        fun setStartId(viewIdAsText: String) {
            if(paramS().isShowTooltipInNextTime) {
//            navigateMain(R.id.WalkthroughStepAF, 1)
                setNextId(viewIdAsText)
                paramS().isShowTooltipInNextTime = false
            }
        }
        fun setNextId(viewIdAsText: String?) {
            mTooltipNextId = viewIdAsText
        }

        fun setNextTime() {
            paramS().cntTooltipShow += 1
            if(paramS().cntTooltipShow < 103) {
                paramS().isShowTooltipInNextTime = true
            }
        }


        private fun getParentRecycler(smartROView: ITooltip): RecyclerView {
            return RecyclerView(this@PServeAct)
        }
        fun setSpecialProcessingForRecycler(smartROView: ITooltip) {
            val rv = getParentRecycler(smartROView)
            rv.let {
                it.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // At this point the layout is complete and the
                            // dimensions of recyclerView and any child views
                            this@PServeAct.onNewfromAFragment()
                            // are known.
                            rvCurrentTask.viewTreeObserver
                                .removeOnGlobalLayoutListener(this)
                        }
                    })
            }
        }
    }


}