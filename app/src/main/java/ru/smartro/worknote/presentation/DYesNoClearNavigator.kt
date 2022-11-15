package ru.smartro.worknote.presentation

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInformFD
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.work.PlatformEntity

class DYesNoClearNavigator : AFragmentYesNoDialog() {
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