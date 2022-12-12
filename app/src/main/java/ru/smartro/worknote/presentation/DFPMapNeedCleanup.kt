package ru.smartro.worknote.presentation

import androidx.fragment.app.activityViewModels
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AInformDF

class DFPMapNeedCleanup: AInformDF() {
    private val viewModel: VMPserve by activityViewModels()

    override fun onGetEntity(): PlatformEntity {
        val platformId = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        val entity = viewModel.database.getPlatformEntity(platformId)
        return entity
    }

    override fun onGetNavId(): Int {
        return R.id.PMapNeedCleanupDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.need_cleanup)
    }

    override fun onNextFragment(entity: PlatformEntity) {
        viewModel.setPlatformEntity(entity)
        navigateNext(R.id.PhotoBeforeMediaF, entity.platformId)
    }
}