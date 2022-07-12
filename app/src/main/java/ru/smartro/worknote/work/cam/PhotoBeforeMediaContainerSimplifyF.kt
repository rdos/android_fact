package ru.smartro.worknote.work.cam


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerSimplifyF : APhotoFragment() {
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
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
            mPlatformEntity = it
        }
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>, isRequireClean: Boolean) {
        navigateMain(R.id.PhotoAfterMediaContainerSimplifyF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetTextLabelFor() = "Контейнер: Фото до"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false


}