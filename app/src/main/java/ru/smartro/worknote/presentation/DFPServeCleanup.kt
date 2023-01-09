package ru.smartro.worknote.presentation

import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AInformDF

class DFPServeCleanup: AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFPServeCleanup
    }

    private val viewModel: VMPserve by activityViewModels()

    override fun onGetEntity(): PlatformEntity {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        if(viewModel.getPlatformEntity().platformId == platformId) {
            LOG.debug("viewModel.getPlatformEntity().platformId == platformId")
            return viewModel.getPlatformEntity()
        }
        val entity = viewModel.database.getPlatformEntity(platformId)
        return entity
    }

    override fun onGetContentText(): String {
        return getString(R.string.need_cleanup)
    }

    override fun onNextFragment(entity: PlatformEntity) {
        navigateNext(FPhotoAfterMedia.NAV_ID, entity.platformId)
    }
}