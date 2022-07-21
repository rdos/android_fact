package ru.smartro.worknote.presentation.cam


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerSimplifyF : APhotoFragment() {

    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "Контейнер: Фото до"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.beforeMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "beforeMediaContainer"
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
        viewModel.baseDat.addBeforeMediaSimplifyServe(mPlatformEntity?.platformId!!, imageS)
        navigateMain(R.id.PhotoAfterMediaContainerSimplifyF, mPlatformEntity?.platformId)
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