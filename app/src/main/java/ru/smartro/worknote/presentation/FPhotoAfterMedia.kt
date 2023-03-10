package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity

import java.io.File

open class FPhotoAfterMedia : APhotoF() {
//    private var mPlatformEntity: PlatformEntity = TODO()
//        get() =  vm.getPlatformEntity()
//GHП!РИКОЛ!!
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
//    override fun onGetTextLabelFor() = "фото после обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.afterMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + App.Companion.PhotoTypeMapping.AFTER_MEDIA
    }

    override fun onBeforeUSE() {
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.addAfterMedia(imageS)
        viewModel.updatePlatformStatusSuccess()
        navigateBack(FPMap.NAV_ID)
    }

    override fun onSavePhoto() {

    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }

    companion object {
        const val NAV_ID = R.id.FPhotoAfterMedia
    }
}