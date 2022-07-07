package ru.smartro.worknote.work.cam

import android.app.Activity
import android.util.Log
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.ActNOAbst
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.util.PhotoTypeEnum
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.cam.APhotoFragment

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
    PhotoTypeEnum.forAfterMedia -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = count + getCountAfterMedia(platform)
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forBeforeMedia -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = count + getCountBeforeMedia(platform)
    mImageCounter?.text = "$mediaSize"
    }
    PhotoTypeEnum.forServedKGO -> {
    val platform = viewModel.baseDat.getPlatformEntity(platformId)
    mediaSize = platform.getServedKGOMediaSize() + count

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
    if(photoFor == PhotoTypeEnum.forPlatformPickupVolume){
    mRootView.findViewById<Button>(R.id.btn_cancel).visibility = View.VISIBLE
    }
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
            photoType == PhotoTypeEnum.forServedKGO -> {
                requireActivity().setResult(101)
            }

            photoType == PhotoTypeEnum.forRemainingKGO -> {
                requireActivity().setResult(102)
            }

            photoType == PhotoTypeEnum.forPlatformPickupVolume -> {
                if(resultCode != -1)
                    requireActivity().setResult(resultCode)
                else
                    requireActivity().setResult(14)
            }

            photoType != PhotoTypeEnum.forBeforeMedia -> {
                requireActivity().setResult(Activity.RESULT_OK)
            }
        }

        requireActivity().finish()
    }

    override fun onGetImageCounter(): Int {
        val mediaSize = when (photoFor) {
            PhotoTypeEnum.forBeforeMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                getCountBeforeMedia(platform)
            }
            PhotoTypeEnum.forAfterMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                getCountAfterMedia(platform)
            }
            PhotoTypeEnum.forPlatformProblem -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.failureMedia.size
            }
            PhotoTypeEnum.forContainerFailure -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.failureMedia.size
            }
            PhotoTypeEnum.forContainerBreakdown -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.breakdownMedia.size
            }

            PhotoTypeEnum.forServedKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getServedKGOMediaSize()
            }

            PhotoTypeEnum.forRemainingKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getRemainingKGOMediaSize()
            }

            PhotoTypeEnum.forPlatformPickupVolume -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getPickupMediaSize()
            }
            else -> 0
        }

        return mediaSize
    }

    override fun onInitViewS() {
        //todo:!!!r_dos
        mIsNoLimitPhoto = requireActivity().intent.getBooleanExtra("isNoLimitPhoto", false)
        Log.w(TAG, "mIsNoLimitPhoto=${mIsNoLimitPhoto}")
    }

    override fun isCurrentMediaIsFull(): Boolean {
        val res = when (photoFor) {
            PhotoTypeEnum.forAfterMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                //todo: фильтер конечно же...!!!
                getCountAfterMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
            }
            PhotoTypeEnum.forBeforeMedia -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                //todo: фильтер конечно же лучше переписать)))) !!!
                getCountBeforeMedia(platform) >= if(mIsNoLimitPhoto) Int.MAX_VALUE else maxPhotoCount
            }
            PhotoTypeEnum.forPlatformProblem -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.failureMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forContainerFailure -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.failureMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forContainerBreakdown -> {
                val container = viewModel.baseDat.getContainerEntity(containerId)
                container.breakdownMedia.size >= maxPhotoCount
            }
            PhotoTypeEnum.forServedKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getServedKGOMediaSize() >= maxPhotoCount
            }
            PhotoTypeEnum.forRemainingKGO -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.getRemainingKGOMediaSize() >= maxPhotoCount
            }
            PhotoTypeEnum.forPlatformPickupVolume -> {
                val platform = viewModel.baseDat.getPlatformEntity(platformId)
                platform.pickupMedia.size >= maxPhotoCount
            }
            else -> {
                false
            }
        }

        return res
    }

    override fun onmThumbNailClick() {
        val fragment = PhotoShowFragment(platformId, containerId, photoFor)
        requireActivity().supportFragmentManager.beginTransaction().run {
            this.replace(R.id.fragment_container, fragment)
            this.addToBackStack(null)
            this.commit()
        }
    }

    override fun onBtnAcceptPhoto_know1() {
        if (photoFor == PhotoTypeEnum.forAfterMedia) {
            (requireActivity() as ActNOAbst).showingProgress()
        }
        activityFinish(photoFor)
    }
}