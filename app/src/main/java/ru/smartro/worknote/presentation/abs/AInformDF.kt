package ru.smartro.worknote.presentation.abs

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.LOG
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.ac.AConfirmDF

//todo: FYI: AbstractInfoDF=BaseInfoDF
abstract class AInformDF: AConfirmDF() {

    override fun onGetEntity(): PlatformEntity? {
        LOG.warn("DON'T_USE") //not use
        return null
    }

   final override fun onLiveData(entity: PlatformEntity) {
        LOG.warn("DON'T_USE")   //not use
    }

    override fun onGetNextText(): String? {
        return "Подтвердить"
    }

    override fun onGetBackText(): String? {
        return null
    }

    override fun onNextFragment(entity: PlatformEntity) {
       navigateBack()
    }

    final override fun onBackFragment(entity: PlatformEntity) {
        LOG.warn("DON'T_USE")   //not use
    }

    final override fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton) {
        acbGotoBack.visibility = View.GONE

    }
    final override fun onBindLayoutState(): Boolean {
//        TODO("Not yet implemented")
        return false
    }

}