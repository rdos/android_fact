package ru.smartro.worknote.presentation.platform_serve

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.Snull
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.andPOintD.ITooltip
import ru.smartro.worknote.toast

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

    inner class TooltipHell : AbsObject(TAG, "ImageEntityScanner") {
        private var mLayoutParams: ViewGroup.LayoutParams? = null
        private var mSaveObj: View? = null
        private var mSaveVG: ViewGroup? = null
        open fun gogogo(){
            val builder = AlertDialog.Builder(this@PServeAct)
            val inflater = this@PServeAct.layoutInflater

            val vG = (window.decorView.rootView   as ViewGroup)
            log("getWindow(:${vG.id}")
            log("getWindow(:${vG.tag}")
            log("getWindow(:::")

//        for (idx in 0 until vG.childCount) {
//            log("getWindow(.getChildAt($idx)")
//            log("getWindow(.getChildAt($idx).id=${vG.getChildAt(idx).id}")
//        }



            val view = inflater.inflate(R.layout.dialog_act_map_tooltip_cheat__alert, null, false)
            builder.setView(view)
            // Установите заголовок
            // Установите заголовок
            val tomDialog = builder.create()
            tomDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val ll = view.findViewById<LinearLayoutCompat>(R.id.privet)
            val findView = fractalS(vG.childCount, vG, ll)
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

            tomDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            tomDialog.window?.setDimAmount(0.8f); //0 for no dim to 1 for full dim
            tomDialog.show()
            tomDialog.setOnDismissListener {
                ll.removeView(mSaveObj)
                val viewGroupObjects: Map<View, View> = mutableMapOf()
                mSaveVG?.addView(mSaveObj, mSaveIndex!!)

                mSaveObj?.layoutParams = mLayoutParams
            }
        }

        private fun fractalS(childCnt: Int, currentVG: ViewGroup, ll: LinearLayoutCompat) {
            for (idx in 0 until childCnt) {
                val obj = currentVG.getChildAt(idx)
//            obj.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            obj.background = null



                if ( obj is ViewGroup) {
                    val Vg = obj as ViewGroup
                    log("getWindow($idx)Vg.childCount=${Vg.childCount}")
                    log("getWindow($idx)Vg.childCount=${Vg.tag}")

                    fractalS(Vg.childCount, Vg, ll)
                } else {
                    if(obj != null && obj.tag != null) {
                        if (obj.tag.toString() == "button_increase_cont") {
                            if (obj is ITooltip) {

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
                                log("getWindow(.obj.getTooltipNext()=${obj.getTooltipNext()}")
                                log("getWindow(.obj.getTooltipType()=${obj.getTooltipType()}")
                            }
                        }
                        log("getWindow(.obj.id=${obj.id}")
                        log("getWindow(:${obj.tag}")
                        log("getWindow(:::")
                    }


                }
            }
        }
    }


}