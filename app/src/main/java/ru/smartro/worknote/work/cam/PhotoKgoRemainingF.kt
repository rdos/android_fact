package ru.smartro.worknote.work.cam

import android.view.View
import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoKgoRemainingF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
    }



    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoRemaining"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.getRemainingKGOMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>, isRequireClean: Boolean) {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
    }

    override fun onGetTextLabelFor() = "Крупногабаритные отходы.заказать борт"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

}