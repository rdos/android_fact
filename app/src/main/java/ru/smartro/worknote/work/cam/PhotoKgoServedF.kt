package ru.smartro.worknote.work.cam

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoKgoServedF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }
    override fun onSavePhoto() {
//        TODO("Not yet implemented")
    }



    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoServed"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.getServedKGOMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }


    override fun onAfterUSE(imageS: List<ImageEntity>, isRequireClean: Boolean) {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return
        }
        val photoFileScanner = PhotoFileScanner(getOutputD())
        while (photoFileScanner.scan()) {
            val imageEntity = photoFileScanner.getImageEntity()
//            viewModel.baseDat.addKgoServed(mPlatformEntity?.platformId!!, imageEntity, isRequireClean)
        }
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
    }

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        navigateBack()
    }

    override fun onGetTextLabelFor() = "Крупногабаритные отходы.забрал"

    override fun onGetIsVisibleBtnCancel() = false

}