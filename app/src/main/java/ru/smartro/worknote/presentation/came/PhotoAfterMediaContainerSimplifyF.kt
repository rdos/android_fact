package ru.smartro.worknote.presentation.came


import androidx.navigation.fragment.findNavController
import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.THR
import java.io.File

// TODO:: NO DIF FROM PhotoAfterMediaContainerF.kt
class PhotoAfterMediaContainerSimplifyF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "фото контейнера после"

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
        if(viewModel.mPlatformEntity.value == null)
            throw Exception("${this::class.java.simpleName}//onBeforeUse//viewModel.mPlatformEntity.value == null")
        mPlatformEntity = viewModel.mPlatformEntity.value
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.baseDat.addAfterMediaSimplifyServe(mPlatformEntity?.platformId!!, imageS)
        findNavController().popBackStack(R.id.PServeF, false)
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