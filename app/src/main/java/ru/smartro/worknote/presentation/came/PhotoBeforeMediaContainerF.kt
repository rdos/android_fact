package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerF : APhotoFragment() {

    private var mPlatformEntity: PlatformEntity? = null

    override fun onGetTextLabelFor() = "контейнер: фото до"
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
//        TODO("Not yet implemented")
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.baseDat.addBeforeMediaSimplifyServe(mPlatformEntity?.platformId!!, imageS)
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId!!,"EXTENDED")
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetIsVisibleBtnCancel() = false
    override fun onClickBtnCancel() {}

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }
}