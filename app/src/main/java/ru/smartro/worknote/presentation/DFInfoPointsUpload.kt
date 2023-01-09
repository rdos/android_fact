package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoPointsUpload : AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoPointsUpload
    }

    override fun onGetContentText(): String {
        return  getString(R.string.different_unload_points)
    }

    override fun onNextFragment(entity: PlatformEntity) {

    }

}