package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ContainerEntity
import ru.smartro.worknote.toast
import ru.smartro.worknote.log.todo.ImageInfoEntity
import java.io.File

class FPhotoBreakdownMediaContainer : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoBreakdownMediaContainer
    }

    private var mBreakDownReasonS: List<String>? = null

    private val mContainerId: Int
        get() = getArgumentID()

    private val mContainerEntity: ContainerEntity
        get() {
            return viewModel.getContainer(mContainerId)
        }
    override fun onGetTextForFailHint() = "Причина поломки контейнера"
    override fun onGetStringList(): List<String> {
        mBreakDownReasonS = viewModel.database.findAllBreakDownReasonS()
        if (mBreakDownReasonS == null || mBreakDownReasonS?.size == 0) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mBreakDownReasonS!!
    }
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mContainerEntity.breakdownMedia
    }

    override fun onGetIsVisibleComment(): Boolean = true

    override fun onGetDirName(): String {
        val containerId = getArgumentID().toString()
        val platformId = getArgumentName()
        return platformId + File.separator + containerId + File.separator + App.Companion.PhotoTypeMapping.BREAKDOWN_MEDIA
    }

    override fun onBeforeUSE() {
        tvLabelFor(requireView())
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        LOG.debug(":P:onSavePhoto")
    }

    var breakdownText: String? = null
    override fun onGotoNext(): Boolean {
        val result = true
        if (mAcactvFail?.text.isNullOrEmpty()) {
            toast("Выберите причину невывоза")
            return false
        }
        breakdownText = mAcactvFail?.text.toString()
        return result
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        val platformId = getArgumentName()?.toInt()!!
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        //        val problemComment = problem_comment.text.toString()
        viewModel.addBreakdownMediaContainer(mContainerId, imageS)
        viewModel.updateContainerBreakDown(mContainerId, breakdownText!!, getCommentText())
        navigateNext(FPServe.NAV_ID, platformId)
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }
}