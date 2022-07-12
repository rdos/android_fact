package ru.smartro.worknote.work.cam


import ru.smartro.worknote.R
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoAfterMediaContainerSimplifyF : PhotoAfterMediaF() {
    companion object {
    }
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "Контейнер: Фото после"
    override fun onSavePhoto() {
//        TODO("Not yet implemented")
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMedia"
    }

    override fun onBeforeUSE() {
//        TODO("Not yet implemented")
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)

    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>, isRequireClean: Boolean) {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }


    override fun onGetIsVisibleBtnCancel() = false
    override fun onClickBtnCancel() {

    }




}