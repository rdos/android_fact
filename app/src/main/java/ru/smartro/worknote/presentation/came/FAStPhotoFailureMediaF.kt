package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class FAStPhotoFailureMediaF : PhotoFailureMediaF() {

    override fun onBackPressed() {
        super.dropOutputD()
        navigateClose()
    }

}