package ru.smartro.worknote.presentation

import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADF
import ru.smartro.worknote.abs.AF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.ac.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.tryCatch

class DFModeUnloadStart: ADF() {
    //    https://en.wikipedia.org/wiki/Virtual_machine :))))))
    private val vm: VMPserve by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.df_unload_start
    }


    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val acbStart = sview.findViewById<SmartROviewSwipeButton>(R.id.sv__f_unload_info__swipe_button)
        acbStart.onSwipe = {

            toast("включился режим Выгрузки")
            vm.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, true)
            val unloadEntity = vm.getPlatformEntity().ploadUnloadEntity()
            vm.database.addPlatformUnloadEntity(vm.getPlatformEntity())

            tryCatch {
                val navHostFragment = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
                val fragmentPMap = (navHostFragment.childFragmentManager.fragments[0] as FPMap)
                fragmentPMap.buildNavigatorPlatformUnload()
                fragmentPMap.toggleUnloadButton(true)
            }
            navigate(R.id.MapPlatformsF)
        }
        return true
    }

    override fun onLiveData() {

    }

    override fun onBindLayoutState(): Boolean{
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }

    override fun onResume() {
        super.onResume()
        //todo:r_dos:: сделать прозрачными
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.height = FrameLayout.LayoutParams.WRAP_CONTENT
        params?.width = FrameLayout.LayoutParams.MATCH_PARENT
        params?.horizontalMargin = 81f

//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes = params
    }
}