package ru.smartro.worknote.presentation

import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ADFragment
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.andPOintD.swipebtn.SmartROviewSwipeButton
import ru.smartro.worknote.presentation.platform_serve.ServePlatformVM
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ConfigName
//todo: смотри прикол, VT !!!UnloadInfo++ploadTicket
class UnloadTicketF: ADFragment() {
    private val viewModel: ServePlatformVM by activityViewModels()

    override fun onGetLayout(): Int {
        return R.layout.f_unload_ticket
    }
    
    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        val acbStart = sview.findViewById<SmartROviewSwipeButton>(R.id.sv__f_unload_info__swipe_button)
        acbStart.mOnReachEnd = {
            val isModeUnload = viewModel.database.getConfigBool(ConfigName.AAPP__IS_MODE__UNLOAD)

            if (isModeUnload) {
                toast("выключился режим Выгрузки")
            } else {
                toast("включился режим Выгрузки")
            }
            viewModel.database.setConfig(ConfigName.AAPP__IS_MODE__UNLOAD, !isModeUnload)

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