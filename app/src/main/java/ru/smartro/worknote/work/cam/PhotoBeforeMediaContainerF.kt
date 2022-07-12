package ru.smartro.worknote.work.cam

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerF : APhotoFragment() {
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

    override fun onGetTextLabelFor() = "Фотографии до обслуживания контейнера"
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