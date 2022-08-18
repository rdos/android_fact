package ru.smartro.worknote.presentation.came


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerByTypesF : APhotoFragment() {

        private val mPlatformEntity: PlatformEntity
        get() =  vm.getPlatformEntity()
    override fun onGetTextLabelFor() = "фото контейнера до"
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
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        vm.database.addBeforeMediaSimplifyServe(mPlatformEntity?.platformId!!, imageS)
        navigateMain(R.id.PServeByTypesF, mPlatformEntity?.platformId)
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