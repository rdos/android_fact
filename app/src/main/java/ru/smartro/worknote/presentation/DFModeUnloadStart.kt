package ru.smartro.worknote.presentation

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADF
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.work.ConfigName

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
            findNavController().previousBackStackEntry?.savedStateHandle?.set("buildNavigatorPlatformUnload", true)
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
}