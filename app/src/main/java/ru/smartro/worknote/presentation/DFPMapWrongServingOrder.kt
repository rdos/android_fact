package ru.smartro.worknote.presentation

import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.presentation.ac.AConfirmDF
import ru.smartro.worknote.ac.SmartROllc
import ru.smartro.worknote.log.todo.PlatformEntity

open class DFPMapWrongServingOrder : AConfirmDF() {

    companion object {
        const val NAV_ID = R.id.DFPMapWrongServingOrder
    }

    val viewModel: VMPserve by activityViewModels()

    override fun onGetEntity(): PlatformEntity? {
        LOG.warn("DON'T_USE")   //not use
        return null
    }

    override fun onBindLayoutState(): Boolean {
        return false
    }

    override fun onLiveData(pl: PlatformEntity) {
    }

    override fun onStyle(sview: SmartROllc, acbGotoBack: AppCompatButton) {
        // DO NOTHING
    }

    override fun onNextFragment(entity: PlatformEntity) {
        navigateBack()
    }

    override fun onGetNextText(): String {
        return "Нет"
    }

    override fun onBackFragment(entity: PlatformEntity) {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val platformE = viewModel.database.getPlatformEntity(platformId)
        viewModel.setPlatformEntity(platformE)
        navigateBack()
        navigateNext(DFPMap.NAV_ID)
    }

    override fun onGetBackText(): String {
        return "Да"
    }

    override fun onGetContentText(): String {
        var dialogString = "Вы уверены, что хотите обслужить КП вне ожидаемого порядка?"
        return dialogString
    }

}