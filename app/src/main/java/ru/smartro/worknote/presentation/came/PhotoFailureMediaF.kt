package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

open class PhotoFailureMediaF : APhotoFragment() {

    private var mFailReasonS: List<String>? = null
    private val mPlatformEntity: PlatformEntity
        get() =  vm.getPlatformEntity()

    override fun onGetTextForFailHint() = "Причина невывоза КП"
    override fun onGetStringList(): List<String>? {
        mFailReasonS = vm.getFailReasonS()
        if (mFailReasonS == null) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mFailReasonS
    }

    override fun onGetIsVisibleComment(): Boolean = true

    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformEntity.failureMedia
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
        return getArgumentID().toString() + File.separator + "failureMedia"
    }

    override fun onBeforeUSE() {
       
        tvLabelFor(requireView())
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
        vm.database.addFailureMediaPlatform(mPlatformEntity?.platformId!!, imageS)
//        val problemComment = problem_comment.text.toString()

        vm.database.setStateFailureForPlatform(mPlatformEntity?.platformId!!, failText!!, getCommentText())
        navigateBack(R.id.MapF)
    }


    override fun onGetIsVisibleBtnCancel() = false

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }
}