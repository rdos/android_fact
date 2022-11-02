package ru.smartro.worknote.presentation

import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADFragment
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ConfigName
//todo: смотри прикол, VT !!!UnloadInfo++ploadTicket
class UnloadTicketF: ADFragment() {
//    https://en.wikipedia.org/wiki/Virtual_machine :))))))
    private val vm: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_unload_ticket
    }
    
    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val acibPhotoBefore = sview.findViewById<AppCompatImageButton>(R.id.acib__f_unload_ticket__photo_before)
        acibPhotoBefore.setOnClickListener {
            navigateMain(R.id.UnloadPhotoBeforeMediaF)
        }

        val acibPhotoAfter = sview.findViewById<AppCompatImageButton>(R.id.acib__f_unload_ticket__photo_after)
        acibPhotoAfter.setOnClickListener {
            navigateMain(R.id.UnloadPhotoAfterMediaF)
        }

        val acbFinish: SmartROviewSwipeButton? = sview.findViewById(R.id.acb_f_unload_ticket__finish)
        acbFinish?.onSwipe = {
            toast("выключился режим Выгрузки")
            vm.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, false)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("buildNavigatorPlatformUnload", false)
            navigateBack(R.id.MapPlatformsF)
        }

        return true
    }

    override fun onNewLiveData() {
//        TODO("Not yet implemented")
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onBackPressed() {
        navigateBack()
    }
}