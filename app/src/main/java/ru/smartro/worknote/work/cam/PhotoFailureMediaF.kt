package ru.smartro.worknote.work.cam

import io.realm.RealmList
import kotlinx.android.synthetic.main.act_platform_failure.*
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoFailureMediaF : APhotoFragment() {
    private var mFailReasonS: List<String>? = null
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "Причина невывоза площадки"
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
    }

    override fun onClickBtnCancel() {
        //тут нужно очистить    mPlatformEntity.volumePickup
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "failureMedia"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
//        mPlatformEntity = viewModel.getPlatformEntity(platformId)
        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
            mPlatformEntity = it
        }
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


    override fun onAfterUSE(imageS: List<ImageEntity>, isRequireClean: Boolean) {
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        viewModel.baseDat.addFailureMediaPlatform(mPlatformEntity?.platformId!!, imageS, isRequireClean)
//        val problemComment = problem_comment.text.toString()

        viewModel.baseDat.updateNonPickupPlatform(mPlatformEntity?.platformId!!, failText!!)
        navigateClose()
    }


    override fun onGetIsVisibleBtnCancel() = false

    override fun onBackPressed() {
        super.onBackPressed()
        navigateClose()
        super.dropOutputD()
       /* if (getMediaCount() <= 0) {
            navigateClose()
        } else {
            navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
        }*/
    }
}