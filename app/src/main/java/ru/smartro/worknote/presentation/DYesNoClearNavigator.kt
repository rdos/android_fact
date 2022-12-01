package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AConfirmYesNoDF

class DYesNoClearNavigator : AConfirmYesNoDF() {
    override fun onNextFragment(entity: PlatformEntity) {
//        getAct().logout()
//        buildNavigator(checkPoint)
//        (getAct().supportFragmentManager.findFragmentById(R.id.map_view) as MapPlatformsF).
    }

    override fun onGetContentText(): String {
        return getString(R.string.way_is_exist)
    }

    override fun onGetNavId(): Int {
      return R.id.DYesNoClearNavigator
    }
}