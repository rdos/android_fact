package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DInfoLockedCarF: AFragmentInfoDialog() {
    override fun onGetNavId(): Int {
        return R.id.LockedCarInfoDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.photo_locked)
    }


}