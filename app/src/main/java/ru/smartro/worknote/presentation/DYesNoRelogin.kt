package ru.smartro.worknote.presentation

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import ru.smartro.worknote.Inull
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInformFD
import ru.smartro.worknote.andPOintD.SmartROllc
import ru.smartro.worknote.presentation.work.PlatformEntity

class DYesNoRelogin : AFragmentYesNoDialog() {
    override fun onNextFragment(entity: PlatformEntity) {
        getAct().logout()
    }

    override fun onGetContentText(): String {
        return  "Вы точно хотите выйти?"
    }

    override fun onGetNavId(): Int {
      return R.id.DYesNoRelogin
    }


}