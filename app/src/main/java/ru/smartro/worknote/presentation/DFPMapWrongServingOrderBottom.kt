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

class DFPMapWrongServingOrderBottom : DFPMapWrongServingOrder() {

    override fun onBackFragment(entity: PlatformEntity) {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val platformE = viewModel.database.getPlatformEntity(platformId)
        viewModel.setPlatformEntity(platformE)
        navigateBack()
        navigateNext(R.id.PMapWarnDF, platformId)
    }

    override fun onGetNavId(): Int {
        return R.id.DFPMapWrongServingOrderBottom
    }
}