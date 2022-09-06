package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.MD5

import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.PlatformMediaEntity
import java.io.File

open class PhotoAfterMediaF : APhotoFragment() {
//    private var mPlatformEntity: PlatformEntity = TODO()
//        get() =  vm.getPlatformEntity()
//GHП!РИКОЛ!!
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
//    override fun onGetTextLabelFor() = "фото после обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformMediaEntity.afterMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "afterMedia"
    }

    override fun onBeforeUSE() {
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }


    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.addAfterMedia(imageS)
        viewModel.updatePlatformStatusSuccess()
        navigateBack(R.id.MapF)
    }

    override fun onSavePhoto() {

    }


    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }
    companion object {
    }
}