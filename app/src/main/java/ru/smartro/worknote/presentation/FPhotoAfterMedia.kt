package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity

import ru.smartro.worknote.log.todo.PlatformMediaEntity
import java.io.File

open class FPhotoAfterMedia : APhotoF() {
//    private var mPlatformEntity: PlatformEntity = TODO()
//        get() =  vm.getPlatformEntity()
//GHП!РИКОЛ!!
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
//    override fun onGetTextLabelFor() = "фото после обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
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


    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.addAfterMedia(imageS)
        viewModel.updatePlatformStatusSuccess()
        navigateBack(R.id.MapPlatformsF)
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
    }
}