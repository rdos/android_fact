package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoLockedPhoto: AInformDF() {
    override fun onGetNavId(): Int {
        return R.id.DInfoLockedPhotoF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}