package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class LockedPhotoInfo: AFragmentInfoDialog() {
    override fun onGetNavId(): Int {
        return R.id.LockedPhotoInfoDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}