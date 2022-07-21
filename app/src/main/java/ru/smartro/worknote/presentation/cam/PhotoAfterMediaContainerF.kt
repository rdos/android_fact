package ru.smartro.worknote.presentation.cam


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

// TODO:: NO DIF FROM PhotoAfterMediaContainerSimplifyF.kt
class PhotoAfterMediaContainerF : APhotoFragment() {

    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "Контейнер: Фото после"

    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.afterMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "afterMediaContainer"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
//        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
//            mPlatformEntity = it
//        }
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.baseDat.addAfterMediaSimplifyServe(mPlatformEntity?.platformId!!, imageS)
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId!!,"EXTENDED")
    }

    override fun onSavePhoto() {

    }

    override fun onGetIsVisibleBtnCancel(): Boolean = false

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