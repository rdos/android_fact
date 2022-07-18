package ru.smartro.worknote.work.cam


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoAfterMediaContainerF : PhotoAfterMediaF() {
    companion object {
//        fun newInstance(workOrderId: Any? = null): PhotoBeforeMediaF {
//            workOrderId as Int
//            val fragment = PhotoBeforeMediaF()
//            fragment.addArgument(workOrderId)
//            return fragment
//        }
    }
    private var mPlatformEntity: PlatformEntity? = null

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

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetTextLabelFor() = "Фотографии после обслуживания контейнера"
    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return super.onGetMediaRealmList()
    }
}