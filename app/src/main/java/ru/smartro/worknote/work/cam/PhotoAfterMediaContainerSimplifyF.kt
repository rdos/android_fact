package ru.smartro.worknote.work.cam


import ru.smartro.worknote.R
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoAfterMediaContainerSimplifyF : PhotoAfterMediaF() {
    companion object {
//        fun newInstance(workOrderId: Any? = null): PhotoBeforeMediaF {
//            workOrderId as Int
//            val fragment = PhotoBeforeMediaF()
//            fragment.addArgument(workOrderId)
//            return fragment
//        }
    }
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
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

    override fun onAfterUSE() {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetTextLabelFor() = "Контейнер: Фото после"
    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onmThumbNailClick() {

    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

}