package ru.smartro.worknote.presentation.abs

import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.LOG
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.ac.AConfirmDF

// TODO: //// когда одна кнопка это либо вправо либо влево либо остаться
abstract class AConfirmYesNoDF : AConfirmDF() {

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