package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.log.work.PlatformEntity
import ru.smartro.worknote.presentation.abs.AInformDF

class DInfoPointsUploadF : AInformDF() {
    override fun onGetContentText(): String {
        return  getString(R.string.different_unload_points)
    }

    override fun onGetNavId(): Int {
        return R.id.DInfoPointsUploadF
    }

    override fun onNextFragment(entity: PlatformEntity) {

    }

}