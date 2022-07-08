package ru.smartro.worknote.work.cam

import android.view.View
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.platform_serve.SCREEN_PServeF
import ru.smartro.worknote.presentation.platform_serve.SCREEN_PhotoBeforeMediaF
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaF : APhotoFragment() {
    companion object {
        fun newInstance(workOrderId: Any? = null): PhotoBeforeMediaF {
            workOrderId as Int
            val fragment = PhotoBeforeMediaF()
            fragment.addArgument(workOrderId)
            return fragment
        }
    }
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
//        TODO("Not yet implemented")
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMedia"
    }

    override fun onGetImageCounter(): Int {
        return super.getOutputFileCount()
    }

    override fun onBeforeUSE() {
//        TODO("Not yet implemented")
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)

    }

    override fun onAfterUSE() {
        val platformId = getArgumentID()
        App.getAppliCation().getRouter().navigateTo(SCREEN_PServeF, platformId)
    }

    override fun onInitViewS(mRootView: View) {
//        TODO("Not yet implemented")

    }

    override fun onGetLabelForText() = getString(R.string.service_before)

    override fun onBtnCancelIsVisible() = false

    override fun onmThumbNailClick() {

    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

    override fun isCurrentMediaIsFull(): Boolean {
        return super.getOutputFileCount() >= 3
    }
}