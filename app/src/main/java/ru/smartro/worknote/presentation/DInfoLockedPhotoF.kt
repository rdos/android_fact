package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DInfoLockedPhotoF: AFragmentInfoDialog() {
    override fun onGetNavId(): Int {
        return R.id.DInfoLockedPhotoF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}