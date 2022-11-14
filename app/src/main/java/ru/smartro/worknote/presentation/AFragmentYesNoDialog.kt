package ru.smartro.worknote.presentation

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInformFD
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.work.PlatformEntity

abstract class AFragmentYesNoDialog : AInformFD() {

    final override fun onGetEntity(): PlatformEntity? {
        LOG.warn("DON'T_USE") //not use
        return null
    }

    override fun onGetNextText(): String {
        return "Подтвердить"
    }

    override fun onGetBackText(): String {
        return "Отмена"
    }

    final override fun onLiveData(entity: PlatformEntity) {
        LOG.warn("DON'T_USE")   //not use
    }

    final override fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton) {
//        TODO("Not yet implemented")

    }

    final override fun onBindLayoutState(): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    override fun onBackFragment(entity: PlatformEntity) {
        navigateBack()
    }
}