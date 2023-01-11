package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

open class FPhotoFailureMedia : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoFailureMedia
    }

    private var mFailReasonS: List<String>? = null
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()

    override fun onGetTextForFailHint() = "Причина невывоза КП"
    override fun onGetStringList(): List<String>? {
        mFailReasonS = viewModel.getFailReasonS()
        if (mFailReasonS == null) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mFailReasonS
    }

    override fun onGetIsVisibleComment(): Boolean = true

    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.failureMedia
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        LOG.debug(":P:onSavePhoto")
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
        val failureComment = viewModel.getPlatformEntity().failureComment
        if(failureComment != null)
            setCommentText(failureComment)
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


    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.database.addFailureMediaPlatform(viewModel.getPlatformId(), imageS)
        viewModel.database.setStateFailureForPlatform(viewModel.getPlatformId(), failText!!, getCommentText())
        navigateBack(FPMap.NAV_ID)
    }


    override fun onGetIsVisibleBtnCancel() = false

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }
}