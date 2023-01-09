package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.todo.ContainerMediaEntity
import ru.smartro.worknote.log.todo.ImageInfoEntity
import java.io.File

class FPhotoFailureMediaContainer : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoFailureMediaContainer
    }

    private var mFailReasonS: List<String>? = null
    private val mContainerId: Int
        get() = getArgumentID()
    private val mContainerMediaEntity: ContainerMediaEntity
        get() {
            return viewModel.getContainerMediaEntity(mContainerId)
        }
    override fun onGetTextForFailHint() = "Причина невывоза контейнера"
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
        return mContainerMediaEntity.failureMedia
    }

    override fun onGetDirName(): String {
        val containerId = getArgumentID().toString()
        val platformId = getArgumentName()
        return platformId + File.separator + containerId + File.separator + "failureMediaContainer"
    }

    override fun onBeforeUSE() {
        tvLabelFor(requireView())
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        LOG.debug(":P:onSavePhoto")
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
        viewModel.addFailureMediaContainer(mContainerId, imageS)
        viewModel.updateContainerFailure(mContainerId, failText!!, getCommentText())
        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }

}