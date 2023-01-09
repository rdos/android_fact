package ru.smartro.worknote.presentation

import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.log.todo.PlatformEntity

class DFPMapWrongServingOrderBottom : DFPMapWrongServingOrder() {

    companion object {
        const val NAV_ID = R.id.DFPMapWrongServingOrderBottom
    }

    override fun onBackFragment(entity: PlatformEntity) {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val platformE = viewModel.database.getPlatformEntity(platformId)
        viewModel.setPlatformEntity(platformE)
        navigateBack()
        navigateNext(DFPMapWarn.NAV_ID, platformId)
    }

}