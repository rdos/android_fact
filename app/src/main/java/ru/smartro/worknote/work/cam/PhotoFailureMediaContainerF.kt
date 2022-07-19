package ru.smartro.worknote.work.cam

import androidx.navigation.fragment.findNavController
import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoFailureMediaContainerF : APhotoFragment() {
    private var mFailReasonS: List<String>? = null
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "невывоза контейнера"
    override fun onGetTextForFailHint() = "Причина невывоза контейнера"
    override fun onGetStringList(): List<String>? {
        mFailReasonS = viewModel.getFailReasonS()
        if (mFailReasonS == null) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mFailReasonS
    }
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.failureMedia
    }
    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        log(":P:onSavePhoto")
    }

    override fun onClickBtnCancel() {
        //тут нужно очистить    mPlatformEntity.volumePickup
    }

    override fun onTakePhoto() {
        super.onTakePhoto()

    }

    override fun onGetDirName(): String {
        return getArgumentName() + File.separator + getArgumentID() + File.separator + "failureMediaContainer"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentName()?.toInt()!!
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
        tvLabelFor(view!!)
//        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
//            mPlatformEntity = it
//        }
    }
    var failText: String? = null
    override fun onGotoNext(): Boolean {
        val result = true
        if (mAcactvFail?.text.isNullOrEmpty()) {
            toast("Выберите причину невывоза")
            return false
        }
        failText = mAcactvFail?.text.toString()
        return result
    }


    override fun onAfterUSE(imageS: List<ImageEntity>) {
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        viewModel.baseDat.setStateFailureForContainer(mPlatformEntity?.platformId!!, getArgumentID(), failText!!, imageS)
//        val problemComment = problem_comment.text.toString()
        findNavController().popBackStack()
    }


    override fun onGetIsVisibleBtnCancel() = false

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
        super.dropOutputD()
        /* if (getMediaCount() <= 0) {
             navigateClose()
         } else {
             navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
         }*/
    }

}