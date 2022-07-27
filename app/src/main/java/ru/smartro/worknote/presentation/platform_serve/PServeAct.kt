package ru.smartro.worknote.presentation.platform_serve

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.fragment.NavHostFragment
import org.w3c.dom.Text
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.andPOintD.ITooltip

//todo: INDEterminate)
class PServeAct :
    ActNOAbst() {
    private var mSaveIndex: Int? = null
    val vm: PlatformServeSharedViewModel by viewModels()
    val mTooltipHell = TooltipHell()

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
        if(paramS().isWalkthroughWasShown == false) {
//            navigateMain(R.id.WalkthroughStepAF, 1)
            mTooltipHell.setTooltipNextId("ll_containers_count")
            paramS().isWalkthroughWasShown = true
        }
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
        if(paramS().walkthroughWasShownCnt < 103) {
            paramS().isWalkthroughWasShown = false
        }
    }

    override fun onNewfromAFragment() {
        super.onNewfromAFragment()
        mTooltipHell.getToottimNextId()?.let {
            mTooltipHell.gogogo(mTooltipHell.getToottimNextId()!!)
        }
    }

    inner class TooltipHell : AbsObject(TAG, "ImageEntityScanner") {
        private var mStopScan: Boolean = false
        private var mLayoutParams: ViewGroup.LayoutParams? = null
        private var mSaveObj: View? = null
        private var mSaveVG: ViewGroup? = null
        private var mTooltipNextId: String? = null
        fun getToottimNextId(): String? {
            return mTooltipNextId
        }
        fun gogogo(tooltipNextId: String){
            mStopScan = false

            val builder = AlertDialog.Builder(this@PServeAct)
            val inflater = this@PServeAct.layoutInflater

            val vG = (this@PServeAct.window.decorView.rootView   as ViewGroup)
            log("getWindow(:${vG.id}")
            log("getWindow(:${vG.tag}")
            log("getWindow(:::")

//        for (idx in 0 until vG.childCount) {
//            log("getWindow(.getChildAt($idx)")
//            log("getWindow(.getChildAt($idx).id=${vG.getChildAt(idx).id}")
//        }



            val view = inflater.inflate(R.layout.dialog_act_map_tooltip_cheat__alert, null, false)

            // Установите заголовок
            val actvDialog = view.findViewById<TextView>(R.id.actv_dialog)
            // Установите заголовок

            val ll = view.findViewById<LinearLayoutCompat>(R.id.llc_component_container)
            mSaveIndex = null
            mSaveVG = null
            mSaveObj = null
            mLayoutParams = null
            val findView = fractalS(vG.childCount, vG, ll, tooltipNextId)
            mStopScan = false
            //        findView?.let {
            //            ll.addView(findView)
            //        }

//        tomDialog.setTitle("Заголовок диалога")
            // Передайте ссылку на разметку
            // Передайте ссылку на разметку
//        dialog.setContentView(R.layout.dialog_view)
            // Найдите элемент TextView внутри вашей разметки
            // и установите ему соответствующий текст
            // Найдите элемент TextView внутри вашей разметки
            // и установите ему соответствующий текст
//        val text = dialog.findViewById(R.id.dialogTextView) as TextView
//        text.text = "не ссы это не дорого. моя речь будет "

//        tomDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            builder.setView(view)
            val tomDialog = builder.create()
            view.findViewById<AppCompatButton>(R.id.apb_dialog).setOnClickListener {
                tomDialog.dismiss()
            }
            tomDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            tomDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            tomDialog.window?.setDimAmount(0.8f); //0 for no dim to 1 for full dim
            mSaveObj?.let {
                actvDialog.text = mSaveObj?.tooltipText
                tomDialog.show()

                tomDialog.setOnDismissListener {
                    if (mSaveObj == null) {
                        return@setOnDismissListener
                    }
                    ll.removeView(mSaveObj)
                    val viewGroupObjects: Map<View, View> = mutableMapOf()
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

        private fun fractalS(childCnt: Int, currentVG: ViewGroup, ll: LinearLayoutCompat, tooltipNextID: String) {
            for (idx in 0 until childCnt) {
                val obj = currentVG.getChildAt(idx)
//            obj.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            obj.background = null

                if (obj is ITooltip) {
                    if(obj.getIdText() == tooltipNextID) {
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
                        log("getWindow(.obj.getIdText()=${obj.getIdText()}")
                        mTooltipNextId = obj.getTooltipNext()

                        log("getWindow(.obj.getTooltipNext()=${obj.getTooltipNext()}")
                        log("getWindow(.obj.getTooltipType()=${obj.getTooltipType()}")
                        mStopScan = true
                        return
                    }
                    log("getWindow(.obj.id=${obj.id}")
                    log("getWindow(:${obj.tag}")
                    log("getWindow(:::")
                }

                if ( obj is ViewGroup) {
                    val Vg = obj as ViewGroup
                    log("getWindow($idx)Vg.childCount=${Vg.childCount}")
                    if (mStopScan == false) {
                        log("getWindow($idx)Vg.childCount=${Vg.tag}")
                        fractalS(Vg.childCount, Vg, ll, tooltipNextID)
                    }
                }
            }
        }

        fun setTooltipNextId(s: String) {
            mTooltipNextId = s
        }
    }


}