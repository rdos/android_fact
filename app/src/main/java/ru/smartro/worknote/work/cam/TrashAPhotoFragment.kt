package ru.smartro.worknote.work.cam

import android.util.Log
import android.view.View
import ru.smartro.worknote.Inull
import ru.smartro.worknote.work.PlatformEntity

class TrashAPhotoFragment(val photoFor: Int, val platformId: Int, val containerId: Int) : APhotoFragment() {
    private var mIsNoLimitPhoto: Boolean = false
    /**
    if (photoFor == PhotoTypeEnum.forContainerBreakdown
    || photoFor == PhotoTypeEnum.forContainerFailure
    ) {
    viewModel.baseDat.updateContainerMedia(photoFor, platformId, containerId, imageEntity)
    } else {
    viewModel.baseDat.updatePlatformMedia(photoFor, platformId, imageEntity)
    }
     */

//    protected abstract fun onGetImageCounter()
    /**
    when (photoFor) {
    PhotoTypeEnum.forAfterMedia, PhotoTypeEnum.forSimplifyServeAfter -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = count + getCountAfterMedia(platform)
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forBeforeMedia, PhotoTypeEnum.forSimplifyServeBefore -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = count + getCountBeforeMedia(platform)
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forContainerFailure -> {
    val container = viewModel.baseDat.getContainerEntity(containerId)
    mediaSize = container.failureMedia.size + count
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forContainerBreakdown -> {
    val container = viewModel.baseDat.getContainerEntity(containerId)
    mediaSize = container.breakdownMedia.size + count
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forPlatformProblem -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = platform.failureMedia.size + count
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forServedKGO -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = platform.getServedKGOMediaSize() + count
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forRemainingKGO -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = platform.getRemainingKGOMediaSize() + count
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forPlatformPickupVolume -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = platform.pickupMedia.size + count
    mImageCounter?.text = "$mediaSize"
    val btnCancel = mRootView.findViewById<TextView>(R.id.btn_cancel)
    btnCancel.isVisible = mediaSize <= 0
    mBtnAcceptPhoto?.visibility = if(mediaSize <= 0) View.GONE else View.VISIBLE
    btnCancel.setOnClickListener {
    activityFinish(photoFor, 404)
    }
    }
    }
     */

//    protected abstract fun onInitViewS()
    /**

     */


    private fun getCountAfterMedia(platform: PlatformEntity): Int {
        var res = Inull
        if (mIsNoLimitPhoto) {
            res = platform.afterMedia.size
        } else {
            res = platform.afterMedia.filter {!it.isNoLimitPhoto }.size
        }
        return res
    }

    private fun getCountBeforeMedia(platform: PlatformEntity): Int {
        var res = Inull
        if (mIsNoLimitPhoto) {
            res = platform.beforeMedia.size
        } else {
            res = platform.beforeMedia.filter {!it.isNoLimitPhoto }.size
        }
        return res
    }
    private fun activityFinish(photoType: Int, resultCode: Int = -1) {
        when {
//            photoType == PhotoTypeEnum.forServedKGO -> {
//                requireActivity().setResult(101)
//            }

//            photoType == PhotoTypeEnum.forRemainingKGO -> {
//                requireActivity().setResult(102)
//            }
//
//            photoType == PhotoTypeEnum.forPlatformPickupVolume -> {
//                if(resultCode != -1)
//                    requireActivity().setResult(resultCode)
//                else
//                    requireActivity().setResult(14)
//            }

        }

        requireActivity().finish()
    }

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onSaveFoto() {
        TODO("Not yet implemented")
    }

    override fun onGetDirName(): String {
        TODO("Not yet implemented")
    }

    fun onGetImageCounter(): Int {
        val mediaSize = when (photoFor) {
//            PhotoTypeEnum.forSimplifyServeBefore -> {
//                val platform = viewModel.baseDat.getPlatformEntity(platformId)
//                getCountBeforeMedia(platform)
//            }
//            PhotoTypeEnum.forAfterMedia, PhotoTypeEnum.forSimplifyServeAfter -> {
//                val platform = viewModel.baseDat.getPlatformEntity(platformId)
//                val test = getCountAfterMedia(platform)
//
//                test
//            }
//            PhotoTypeEnum.forPlatformProblem -> {
//                val platform = viewModel.baseDat.getPlatformEntity(platformId)
//                platform.failureMedia.size
//            }
//            PhotoTypeEnum.forContainerFailure -> {
//                val container = viewModel.baseDat.getContainerEntity(containerId)
//
//            }

            else -> 0
        }
        return mediaSize
    }

    override fun onBeforeUSE() {
//        TODO("Not yet implemented")
    }

    override fun onAfterUSE(){
//        TODO("Not yet implemented")
    }

    fun onInitViewS(mRootView: View) {
        //todo:!!!r_dos

//        if(photoFor == PhotoTypeEnum.forSimplifyServeAfter) {
//            mRootView.findViewById<TextView>(R.id.label_for).apply {
//                visibility = View.VISIBLE
//            }
//        }
        mIsNoLimitPhoto = requireActivity().intent.getBooleanExtra("isNoLimitPhoto", false)
        Log.w(TAG, "mIsNoLimitPhoto=${mIsNoLimitPhoto}")
    }

    override fun onGetTextLabelFor(): String {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onmThumbNailClick() {
//        val fragment = PhotoGalleryF(platformId, containerId, photoFor)
//        requireActivity().supportFragmentManager.beginTransaction().run {
//            this.replace(R.id.fragment_container, fragment)
//            this.addToBackStack(null)
//            this.commit()
//        }
    }

    override fun onBtnAcceptPhoto_know1() {
//        if (photoFor == PhotoTypeEnum.forAfterMedia) {
//            (requireActivity() as ActNOAbst).showingProgress()
//        }
//        activityFinish(photoFor)
    }

    val maxPhotoCount = 3
    fun getMaxPhotoCNT(): Boolean {
            val res = when (photoFor) {
///**                PhotoTypeEnum.forAfterMedia,*/ PhotoTypeEnum.forSimplifyServeAfter -> {
//                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
//                    //todo: фильтер конечно же...!!!
//                    getCountAfterMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
//                }
//                PhotoTypeEnum.forSimplifyServeBefore -> {
//                    val platform = viewModel.baseDat.getPlatformEntity(platformId)
//                    //todo: фильтер конечно же лучше переписать)))) !!!
//                    getCountBeforeMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
//                }


                else -> {
                    false
                }
            }
            return res
        }
}