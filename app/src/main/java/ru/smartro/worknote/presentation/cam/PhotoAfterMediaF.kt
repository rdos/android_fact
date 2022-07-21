package ru.smartro.worknote.presentation.cam

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity

import ru.smartro.worknote.work.PlatformEntity
import java.io.File

open class PhotoAfterMediaF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = getString(R.string.service_after)
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.afterMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "afterMedia"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
            mPlatformEntity = it
        }
    }

    override fun onGotoNext(): Boolean {
        return true
    }


    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.baseDat.addAfterMedia(mPlatformEntity?.platformId!!, imageS)
        navigateClose()
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
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